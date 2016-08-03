package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by 27932 on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private final static String TAG = GoodDetailsActivity.class.getCanonicalName();

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

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG, "mGoodId=" + mGoodId);
    }

    private void initView() {
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
}
