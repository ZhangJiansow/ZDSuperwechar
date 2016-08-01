package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by 27932 on 2016/8/1.
 */
public class GoodAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    List<NewGoodBean> mGoodList;
    GoodViewHolder mGoodViewHolder;
    public GoodAdapter(Context context, List<NewGoodBean> list) {
        mContext = context;
        mGoodList = new ArrayList<NewGoodBean>();
        mGoodList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        LayoutInflater inflate = LayoutInflater.from(mContext);
        holder = new GoodViewHolder(inflate.inflate(R.layout.item_good, null, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder) {
            mGoodViewHolder = (GoodViewHolder) holder;
            NewGoodBean good = mGoodList.get(position);
            mGoodViewHolder.tvGoodName.setText(good.getGoodsName());
            mGoodViewHolder.tvGoodPrice.setText(good.getCurrencyPrice());
        }
    }

    @Override
    public int getItemCount() {
        return mGoodList.size();
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
}
