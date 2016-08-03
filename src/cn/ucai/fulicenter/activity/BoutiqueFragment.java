package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by 27932 on 2016/8/3.
 */
public class BoutiqueFragment extends Fragment{
    private final static String TAG = BoutiqueFragment.class.getCanonicalName();
    List<BoutiqueBean> mBoutiqueList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FuliCenterMainActivity mContext;
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext, R.layout.fragment_boutique, null);
        mBoutiqueList = new ArrayList<BoutiqueBean>();
//        initView(layout);
//        initData();
//        setListener();
        return layout;
    }
}
