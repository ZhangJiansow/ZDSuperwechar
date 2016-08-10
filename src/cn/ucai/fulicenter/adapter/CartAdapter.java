package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by 27932 on 2016/8/1.
 */
public class CartAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    List<CartBean> mCartList;
    CartViewHolder mCartViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context context, List<CartBean> list) {
        mContext = context;
        mCartList = new ArrayList<CartBean>();
        mCartList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(mContext);
        ViewHolder holder = new CartViewHolder(inflate.inflate(R.layout.item_cart, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CartViewHolder) {
            mCartViewHolder = (CartViewHolder) holder;
            final CartBean cart = mCartList.get(position);
            mCartViewHolder.cbCartSelected.setChecked(cart.isChecked());
            if (cart.getGoods() != null) {
                ImageUtils.setGoodThumb(mContext, mCartViewHolder.ivCartThumb, cart.getGoods().getGoodsThumb());
                mCartViewHolder.tvCartGoodName.setText(cart.getGoods().getGoodsName());
                mCartViewHolder.tvCartCount.setText("("+cart.getCount()+")");
                mCartViewHolder.tvCartPrice.setText(cart.getGoods().getCurrencyPrice());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCartList!=null?mCartList.size():0;
    }

    public void initData(List<CartBean> list) {
        if (mCartList != null) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(List<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends ViewHolder {
        CheckBox cbCartSelected;
        ImageView ivCartThumb;
        TextView tvCartGoodName;
        ImageView ivCartAdd;
        TextView tvCartCount;
        ImageView ivCartDel;
        TextView tvCartPrice;
        RelativeLayout layout;
        public CartViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.layout.item_cart);
            cbCartSelected = (CheckBox) itemView.findViewById(R.id.cb_cart_selected);
            ivCartThumb = (ImageView) itemView.findViewById(R.id.iv_cart_thumb);
            tvCartGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            ivCartAdd = (ImageView) itemView.findViewById(R.id.iv_cart_add);
            tvCartCount = (TextView) itemView.findViewById(R.id.tv_cart_count);
            ivCartDel = (ImageView) itemView.findViewById(R.id.iv_cart_del);
            tvCartPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
        }
    }
}
