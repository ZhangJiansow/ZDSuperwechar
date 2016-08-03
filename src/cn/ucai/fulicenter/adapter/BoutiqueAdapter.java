package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.ucai.fulicenter.activity.FuliCenterMainActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by 27932 on 2016/8/1.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<ViewHolder>{
    private static final String TAG = BoutiqueAdapter.class.getSimpleName();
    Context mContext;
    List<BoutiqueBean> mBoutiqueList;
    BoutiqueViewHolder mBoutiqueViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;
    final static int FooterType = 0;
    final static int ItemType = 1;
    String FooterText;

    public void setFooterString(String footerString) {
        FooterText = footerString;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public BoutiqueAdapter(Context context, List<BoutiqueBean> list) {
        mContext = context;
        mBoutiqueList = new ArrayList<BoutiqueBean>();
        mBoutiqueList.addAll(list);
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
                holder = new BoutiqueViewHolder(inflate.inflate(R.layout.item_boutique, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof BoutiqueViewHolder) {
            mBoutiqueViewHolder = (BoutiqueViewHolder) holder;
            final BoutiqueBean boutique = mBoutiqueList.get(position);
            ImageUtils.setGoodThumb(mContext, mBoutiqueViewHolder.ivBoutiqueThumb, boutique.getImageurl());
            mBoutiqueViewHolder.tvTitle.setText(boutique.getTitle());
            mBoutiqueViewHolder.tvName.setText(boutique.getName());
            mBoutiqueViewHolder.tvDesc.setText(boutique.getDescription());
            mBoutiqueViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, FuliCenterMainActivity.class)
                            .putExtra(D.Boutique.KEY_ID,boutique.getId()));
                }
            });
        }
        if (holder instanceof FooterViewHolder) {
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(FooterText);
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
        return mBoutiqueList!=null?mBoutiqueList.size()+1:1;
    }

    public void initData(ArrayList<BoutiqueBean> list) {
        if (mBoutiqueList != null) {
            mBoutiqueList.clear();
        }
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<BoutiqueBean> list) {
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    class BoutiqueViewHolder extends ViewHolder {
        RelativeLayout layout;
        ImageView ivBoutiqueThumb;
        TextView tvTitle,tvName,tvDesc;
        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout_boutique_item);
            ivBoutiqueThumb = (ImageView) itemView.findViewById(R.id.ivBoutiqueImg);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBoutiqueTitle);
            tvName = (TextView) itemView.findViewById(R.id.tvBoutiqueName);
            tvDesc = (TextView) itemView.findViewById(R.id.tvBoutiqueDescription);
        }
    }
}
