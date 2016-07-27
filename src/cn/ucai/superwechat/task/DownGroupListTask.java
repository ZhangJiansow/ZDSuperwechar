package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

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
                            SuperWeChatApplication.getInstance().setGroupList(list);
                            for (GroupAvatar g : list) {
                                SuperWeChatApplication.getInstance().getGroupMap().put(g.getMGroupHxid(), g);
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
