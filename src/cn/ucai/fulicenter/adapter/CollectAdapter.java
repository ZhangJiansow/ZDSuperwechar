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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by 27932 on 2016/8/1.
 */
public class CollectAdapter extends RecyclerView.Adapter<ViewHolder>{
    private final static String TAG = CollectAdapter.class.getSimpleName();
    Context mContext;
    List<CollectBean> mGoodList;
    CollectViewHolder mCollectViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;
    String footerString;
    int sortBy;

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
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

    public CollectAdapter(Context context, List<CollectBean> list) {
        mContext = context;
        mGoodList = new ArrayList<CollectBean>();
        mGoodList.addAll(list);
        sortBy = I.SORT_BY_ADDTIME_DESC;
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
                holder = new CollectViewHolder(inflate.inflate(R.layout.item_collect, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CollectViewHolder) {
            mCollectViewHolder = (CollectViewHolder) holder;
            final CollectBean collect = mGoodList.get(position);
            ImageUtils.setGoodThumb(mContext, mCollectViewHolder.ivGoodThumb, collect.getGoodsThumb());
            mCollectViewHolder.tvGoodName.setText(collect.getGoodsName());
            mCollectViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                    .putExtra(D.GoodDetails.KEY_GOODS_ID,collect.getGoodsId()));
                }
            });
            mCollectViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                    utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                            .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                            .addParam(I.Collect.GOODS_ID,String.valueOf(collect.getGoodsId()))
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    Log.e(TAG, "result=" + result);
                                    if (result != null) {
                                        mGoodList.remove(collect);
                                        new DownCollectCountTask(FuLiCenterApplication.getInstance().getUserName(),mContext).execute();
                                        notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, "删除失败");
                                    }
                                    Toast.makeText(mContext,result.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG, "error=" + error);
                                }
                            });
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

    public void initData(ArrayList<CollectBean> list) {
        if (mGoodList != null) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<CollectBean> list) {
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    class CollectViewHolder extends ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        ImageView ivDelete;
        public CollectViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_collect_delete);
        }
    }
}
