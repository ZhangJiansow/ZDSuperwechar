package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by 27932 on 2016/8/1.
 */
public class CollectActivity extends BaseActivity{
    private final static String TAG = CollectActivity.class.getCanonicalName();
    CollectActivity mContext;
    List<CollectBean> mCollectList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mAdapter;

    int pageId = 0;
    int action=I.ACTION_DOWNLOAD;
    TextView tvHint;

    int catId = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_collect);
        mCollectList = new ArrayList<CollectBean>();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        setUpdateCollectListListener();
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;//1
                int b = RecyclerView.SCROLL_STATE_IDLE;//0
                int c = RecyclerView.SCROLL_STATE_SETTLING;//2
                Log.e(TAG, "newState=" + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE&&lastItemPosition==mAdapter.getItemCount()-1) {
                    if (mAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId += I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstPosition = mGridLayoutManager.findFirstVisibleItemPosition();
                int lastPosition = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "firstPosition=" + firstPosition + ",lastPosition=" + lastPosition);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstVisibleItemPosition() == 0);
                if (firstPosition == -1 || lastPosition == -1) {
                    lastItemPosition = mAdapter.getItemCount() - 1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                tvHint.setVisibility(View.VISIBLE);
                pageId = 0;
                initData();
            }
        });
    }

    private void initData() {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        Log.e(TAG, "userName=" + userName);
        if (userName.isEmpty()) {
            finish();
        }
        findCollectList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
            @Override
            public void onSuccess(CollectBean[] result) {
                Log.e(TAG, "result=" + result);
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterString(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<CollectBean> collectList = Utils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(collectList);
                    } else {
                        mAdapter.addItem(collectList);
                    }
                    if (collectList.size() < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterString(getResources().getString(R.string.no_more));
                    }
                } else {
                    mAdapter.setMore(false);
                    mAdapter.setFooterString(getResources().getString(R.string.no_more));
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "error=" + error);
                tvHint.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void findCollectList(OkHttpUtils2.OnCompleteListener<CollectBean[]> listener) {
        OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<CollectBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CollectBean[].class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBackwithTitle(mContext,"收藏的宝贝");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_collect);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(mContext, mCollectList);
        mRecyclerView.setAdapter(mAdapter);

        tvHint = (TextView) findViewById(R.id.tv_refresh_hint);
    }

    class UpdateCollectListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }

    UpdateCollectListReceiver mReceiver;

    private void setUpdateCollectListListener() {
        mReceiver = new UpdateCollectListReceiver();
        IntentFilter filter = new IntentFilter("update_collect_list");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
