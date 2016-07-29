package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.GroupAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownGroupListTask {
    private final static String TAG = DownGroupListTask.class.getSimpleName();
    String username;
    Context mContext;


    public DownGroupListTask(String username, Context context) {
        mContext = context;
        this.username = username;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s" + s);
                        Result result = Utils.getListResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result=" + result);
                        List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                        if ((list != null) && (list.size() > 0)) {
                            Log.e(TAG, "list.size=" + list.size());
                            FuLiCenterApplication.getInstance().setGroupList(list);
                            for (GroupAvatar g : list) {
                                FuLiCenterApplication.getInstance().getGroupMap().put(g.getMGroupHxid(), g);
                            }
                            mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
