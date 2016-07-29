package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * Created by 27932 on 2016/7/29.
 */
public class MessageBean implements Serializable {

    /**
     * success : true
     * msg : 添加收藏成功
     */

    private boolean success;
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
