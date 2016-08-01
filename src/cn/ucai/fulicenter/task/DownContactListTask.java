package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/7/21.
 */
public class DownContactListTask {
    private final static String TAG = DownContactListTask.class.getSimpleName();
    String username;
    Context mContext;


    public DownContactListTask(String username, Context context) {
        mContext = context;
        this.username = username;
    }

    public void execute() {
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s" + s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        Log.e(TAG, "result=" + result);
                        if (result != null) {
                            List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                            Log.e(TAG, "list=" + list);
                            if ((list != null) && (list.size() > 0)) {
                                FuLiCenterApplication.getInstance().setUserList(list);
                                mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                                Map<String, UserAvatar> userMap = FuLiCenterApplication.getInstance().getUserMap();
                                for (UserAvatar u : list) {
                                    userMap.put(u.getMUserName(),u);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error" + error);
                    }
                });
    }
}
