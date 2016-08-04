package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by 27932 on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity{
    private static final String TAG = FuliCenterMainActivity.class.getSimpleName();
    RadioButton rbNewGood,rbBoutique,rbCategory,rbCart,rbPersonal;
    TextView tvCartHint;
    RadioButton[] mrbTabs;

    int index;
    int currentIndex;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;

    Fragment[] fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        fragments = new Fragment[] { mNewGoodFragment, mBoutiqueFragment,mCategoryFragment};
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container,mCategoryFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initView() {
        rbNewGood = (RadioButton) findViewById(R.id.ivNewGoods);
        rbBoutique = (RadioButton) findViewById(R.id.ivBoutique);
        rbCategory = (RadioButton) findViewById(R.id.ivCategory);
        rbCart = (RadioButton) findViewById(R.id.ivCart);
        rbPersonal = (RadioButton) findViewById(R.id.ivPersonal);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGood;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPersonal;
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.ivNewGoods:
                index = 0;
                break;
            case R.id.ivBoutique:
                index = 1;
                break;
            case R.id.ivCategory:
                index = 2;
                break;
            case R.id.ivCart:
                index = 3;
                break;
            case R.id.ivPersonal:
                index = 4;
                break;
        }
        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mrbTabs[currentIndex].setSelected(false);
        // 把当前tab设为选中状态
        mrbTabs[index].setSelected(true);
        currentIndex = index;
        Log.e(TAG, "index=" + index + ",currentIndex=" + currentIndex);
        if (index != currentIndex) {
            setRadioButtonStatus(index);
            currentIndex = index;
        }
    }

    private void setRadioButtonStatus(int index) {
        for (int i=0;i<mrbTabs.length;i++) {
            if (i == index) {
                mrbTabs[i].setChecked(true);
            } else {
                mrbTabs[i].setChecked(false);
            }
        }
    }
}
