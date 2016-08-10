package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownCollectCountTask;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by 27932 on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private final static String TAG = GoodDetailsActivity.class.getCanonicalName();

    GoodDetailsActivity mContext;

    ImageView ivShare;
    ImageView ivCollect;
    ImageView ivCart;
    TextView tvCartCount;

    TextView tvGoodEnglishName;
    TextView tvGoodName;
    TextView tvGoodPriceCurrent;
    TextView tvGoodPriceShop;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;

    int mGoodId;
    GoodDetailsBean mGoodDetails;

    boolean isCollect;

    updateCartNumReceiver mReceiver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        MyOnClickListener listener = new MyOnClickListener();
        ivCollect.setOnClickListener(listener);
        ivShare.setOnClickListener(listener);
        setUpdateCartCountListener();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "mGoodId=" + mGoodId);
        if (mGoodId > 0) {
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
                    Log.e(TAG, "getGoodDetailsByGoodId,result=" + result);
                    if (result != null) {
                        mGoodDetails = result;
                        Log.e(TAG, "getGoodDetailsByGoodId,mGoodDetails=" + mGoodDetails);

                        showGoodDetails();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                    finish();
                    Toast.makeText(mContext,"获取商品详情数据失败！",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
            Toast.makeText(mContext,"获取商品详情数据失败！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示GoodDetailsBean中的数据
     */
    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetails.getGoodsName());
        tvGoodPriceCurrent.setText(mGoodDetails.getCurrencyPrice());
        tvGoodPriceShop.setText(mGoodDetails.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,getAlbumImageUrl(),getAlbumImageSize());
        wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief(), D.TEXT_HTML, D.UTF_8, null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if (mGoodDetails.getProperties() != null && mGoodDetails.getProperties().length > 0) {
            AlbumsBean[] album = mGoodDetails.getProperties()[0].getAlbums();
            albumImageUrl = new String[album.length];
            for (int i=0;i<albumImageUrl.length;i++) {
                albumImageUrl[i] = album[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize() {
        if (mGoodDetails.getProperties() != null && mGoodDetails.getProperties().length > 0) {
            return  mGoodDetails.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    /**
    * 获取GoodDetailsBean中的数据
    */
    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBack(mContext);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_good_cart_count);
        tvGoodEnglishName = (TextView) findViewById(R.id.tv_good_english_name);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        tvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        updateCartNum();
    }

    private void initCollectStatus() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME,userName)
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG, "result=" + result);
                            if (result != null && result.isSuccess()) {
                                isCollect = true;
                            } else {
                                isCollect = false;
                            }
                            updateCollectStatus();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "error=" + error);
                        }
                    });
        }
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_good_collect:
                    goodCollect();
                    break;
                case R.id.iv_good_share:
                    showShare();
                    break;
            }
        }
    }

    private void goodCollect() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (isCollect) {
                OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodId))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                Log.e(TAG, "result=" + result);
                                if (result != null) {
                                    isCollect = false;
                                    new DownCollectCountTask(FuLiCenterApplication.getInstance().getUserName(), mContext).execute();
                                    sendStickyBroadcast(new Intent("update_collect_list"));
                                } else {
                                    Log.e(TAG, "删除失败");
                                }
                                updateCollectStatus();
                                Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "error=" + error);
                            }
                        });
            } else {
                Log.e(TAG,"detail="+mGoodDetails);
                OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                        .addParam(I.Collect.USER_NAME,FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodDetails.getGoodsId()))
                        .addParam(I.Collect.ADD_TIME, String.valueOf(mGoodDetails.getAddTime()))
                        .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetails.getGoodsEnglishName())
                        .addParam(I.Collect.GOODS_THUMB,mGoodDetails.getGoodsThumb())
                        .addParam(I.Collect.GOODS_IMG,mGoodDetails.getGoodsImg())
                        .addParam(I.Collect.GOODS_NAME,mGoodDetails.getGoodsName())
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                Log.e(TAG, "result=" + result);
                                if (result != null && result.isSuccess()) {
                                    isCollect = true;
                                    new DownCollectCountTask(FuLiCenterApplication.getInstance().getUserName(), mContext).execute();
                                    sendStickyBroadcast(new Intent("update_collect_list"));
                                } else {
                                    Log.e(TAG, "删除失败");
                                }
                                updateCollectStatus();
                                Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "error=" + error);
                            }
                        });
            }
        } else {
            startActivity(new Intent(mContext,LoginActivity.class));
        }
    }

    private void updateCollectStatus() {
        if (isCollect) {
            ivCollect.setImageResource(R.drawable.bg_collect_out);
        } else {
            ivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mGoodDetails.getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mGoodDetails.getGoodsName());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mGoodDetails.getShareUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mGoodDetails.getGoodsName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mGoodDetails.getShareUrl());

// 启动分享GUI
        oks.show(this);
    }

    class updateCartNumReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateCartNum();
        }
    }

    private void setUpdateCartCountListener() {
        mReceiver = new updateCartNumReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        registerReceiver(mReceiver, filter);
    }

    private void updateCartNum() {
        int count = Utils.sumCartCount();
        if (!DemoHXSDKHelper.getInstance().isLogined() || count == 0) {
            tvCartCount.setText(String.valueOf(0));
            tvCartCount.setVisibility(View.GONE);
        } else {
            tvCartCount.setText(String.valueOf(count));
            tvCartCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
