package cn.ucai.fulicenter.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by 27932 on 2016/8/1.
 */
public class CategoryChildActivity extends BaseActivity{
    private final static String TAG = CategoryChildActivity.class.getCanonicalName();
    CategoryChildActivity mContext;
    List<NewGoodBean> mGoodList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    GoodAdapter mAdapter;

    Button btnSortPrice;
    Button btnSortAddTime;
    boolean mSortPriceAsc;
    boolean mSortAddTimeAsc;
    int sortBy;

    int pageId = 0;
    int action=I.ACTION_DOWNLOAD;
    TextView tvHint;

    int catId = 0;
    CatChildFilterButton mCatChildFilterButton;
    String name;
    ArrayList<CategoryChildBean> childList;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_category_child);
        mGoodList = new ArrayList<NewGoodBean>();
        sortBy = I.SORT_BY_ADDTIME_DESC;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStatusChangeListener listener = new SortStatusChangeListener();
        btnSortPrice.setOnClickListener(listener);
        btnSortAddTime.setOnClickListener(listener);
        mCatChildFilterButton.setOnCatFilterClickListener(name,childList);
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
        catId = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
        Log.e(TAG, "catId=" + catId);
        childList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        if (catId < 0) {
            finish();
        }
        findBoutiqueChildList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                Log.e(TAG, "result=" + result);
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterString(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<NewGoodBean> goodBeanArrayList = Utils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initData(goodBeanArrayList);
                    } else {
                        mAdapter.addItem(goodBeanArrayList);
                    }
                    if (goodBeanArrayList.size() < I.PAGE_SIZE_DEFAULT) {
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

    private void findBoutiqueChildList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(catId))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView() {
//        String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBack(mContext);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_category_child);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        btnSortPrice = (Button) findViewById(R.id.btn_sort_price);
        btnSortAddTime = (Button) findViewById(R.id.btn_sort_addtime);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        name = getIntent().getStringExtra(I.CategoryGroup.NAME);
        mCatChildFilterButton.setText(name);

        tvHint = (TextView) findViewById(R.id.tv_refresh_hint);
    }

    class SortStatusChangeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Drawable right;
            switch (v.getId()) {
                case R.id.btn_sort_price:
                    if (mSortPriceAsc) {
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortPriceAsc = !mSortPriceAsc;
                    right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
                    btnSortPrice.setCompoundDrawablesWithIntrinsicBounds(null,null,right,null);
                    break;
                case R.id.btn_sort_addtime:
                    if (mSortAddTimeAsc) {
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortAddTimeAsc = !mSortAddTimeAsc;
                    right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
                    btnSortAddTime.setCompoundDrawablesWithIntrinsicBounds(null,null,right,null);
                    break;
            }
            mAdapter.setSortBy(sortBy);
        }
    }
}
