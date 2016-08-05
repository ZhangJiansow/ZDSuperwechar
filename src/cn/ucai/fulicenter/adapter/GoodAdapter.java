package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by 27932 on 2016/8/1.
 */
public class GoodAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    List<NewGoodBean> mGoodList;
    GoodViewHolder mGoodViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;
    String footerString;
    int sortBy;

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        soryBy();
        notifyDataSetChanged();
    }

    public void setFooterString(String footerString) {
        this.footerString = footerString;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public GoodAdapter(Context context, List<NewGoodBean> list) {
        mContext = context;
        mGoodList = new ArrayList<NewGoodBean>();
        mGoodList.addAll(list);
        sortBy = I.SORT_BY_ADDTIME_DESC;
        soryBy();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflate.inflate(R.layout.item_footer, parent, false));
                break;
            case I.TYPE_ITEM:
                holder = new GoodViewHolder(inflate.inflate(R.layout.item_good, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder) {
            mGoodViewHolder = (GoodViewHolder) holder;
            final NewGoodBean good = mGoodList.get(position);
            ImageUtils.setGoodThumb(mContext, mGoodViewHolder.ivGoodThumb, good.getGoodsThumb());
            mGoodViewHolder.tvGoodName.setText(good.getGoodsName());
            mGoodViewHolder.tvGoodPrice.setText(good.getCurrencyPrice());
            mGoodViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                    .putExtra(D.GoodDetails.KEY_GOODS_ID,good.getGoodsId()));
                }
            });
        }
        if (holder instanceof FooterViewHolder) {
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(footerString);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mGoodList!=null?mGoodList.size()+1:1;
    }

    public void initData(ArrayList<NewGoodBean> list) {
        if (mGoodList != null) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        soryBy();
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<NewGoodBean> list) {
        mGoodList.addAll(list);
        soryBy();
        notifyDataSetChanged();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName,tvGoodPrice;
        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }

    private void soryBy() {
        Collections.sort(mGoodList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean goodLeft, NewGoodBean goodRight) {
                int result = 0;
                switch (sortBy) {
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int) (Long.valueOf(goodRight.getAddTime()) - Long.valueOf(goodLeft.getAddTime()));
                        break;
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int) (Long.valueOf(goodLeft.getAddTime()) - Long.valueOf(goodRight.getAddTime()));
                        break;
                    case I.SORT_BY_PRICE_DESC:
                        result = convertPrice(goodRight.getCurrencyPrice()) - convertPrice(goodLeft.getCurrencyPrice());
                        break;
                    case I.SORT_BY_PRICE_ASC:
                        result = convertPrice(goodLeft.getCurrencyPrice()) - convertPrice(goodRight.getCurrencyPrice());
                        break;
                }
                return result;
            }

            private int convertPrice(String price) {
                price = price.substring(price.indexOf("￥") + 1);
                return Integer.valueOf(price);
            }
        });
    }
}
