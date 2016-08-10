package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by 27932 on 2016/8/3.
 */
public class CartFragment extends Fragment{
    private final static String TAG = CartFragment.class.getCanonicalName();
    Context mContext;
    List<CartBean> mCartList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mAdapter;
    TextView tvSumPrice;
    TextView tvSavePrice;
    TextView tvBuy;

    int pageId = 0;
    int action=I.ACTION_DOWNLOAD;
    TextView tvHint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View layout = View.inflate(mContext, R.layout.fragment_cart, null);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
//        setPullDownRefreshListener();
        setPullUpRefreshListener();
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
                int firstPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                int lastPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG, "firstPosition=" + firstPosition + ",lastPosition=" + lastPosition);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstVisibleItemPosition() == 0);
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
        List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        mCartList.clear();
        mCartList.addAll(cartList);
        mSwipeRefreshLayout.setRefreshing(false);
        tvHint.setVisibility(View.GONE);
        mAdapter.setMore(true);
        if (mCartList != null&&mCartList.size()>0) {
            Log.e(TAG, "mCartList.length=" + mCartList.size());
            if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                mAdapter.initData(mCartList);
            } else {
                mAdapter.addItem(mCartList);
            }
            if (mCartList.size() < I.PAGE_SIZE_DEFAULT) {
                mAdapter.setMore(false);
            }
        } else {
            mAdapter.setMore(false);
        }
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);

        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        tvSumPrice = (TextView) layout.findViewById(R.id.tv_cart_sum_price);
        tvSavePrice = (TextView) layout.findViewById(R.id.tv_cart_save_price);
        tvBuy = (TextView) layout.findViewById(R.id.tv_cat_buy);
    }
}
