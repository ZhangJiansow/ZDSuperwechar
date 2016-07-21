package cn.ucai.superwechat.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Pager;
import cn.ucai.superwechat.bean.Result;

/**
 * Created by clawpo on 16/3/28.
 */
public class Utils {
    public static String getPackageName(Context context){
        return context.getPackageName();
    }
    
    public static void showToast(Context context,String text,int time){
        Toast.makeText(context,text,time).show();
    }
    
    public static void showToast(Context context,int  strId,int time){
        Toast.makeText(context, strId, time).show();
    }

    /**
     * 将数组转换为ArrayList集合
     * @param ary
     * @return
     */
    public static <T> ArrayList<T> array2List(T[] ary){
        if(ary==null)return null;
        List<T> list = Arrays.asList(ary);
        ArrayList<T> arrayList=new ArrayList<T>(list);
        return arrayList;
    }

    /**
     * 添加新的数组元素：数组扩容
     * @param array：数组
     * @param t：添加的数组元素
     * @return：返回添加后的数组
     */
    public static <T> T[] add(T[] array,T t){
        array=Arrays.copyOf(array, array.length+1);
        array[array.length-1]=t;
        return array;
    }

    public static String getResourceString(Context context, int msg){
        if(msg<=0) return null;
        String msgStr = msg+"";
        msgStr = I.MSG_PREFIX_MSG + msgStr;
        int resId = context.getResources().getIdentifier(msgStr, "string", context.getPackageName());
        return context.getResources().getString(resId);
    }

//    public static <T> datas getResultDate(Object o,final T t){
//        if(o!=null){
//            Type type = new TypeToken<t>(){}.getClass();
//            return new Gson().fromJson(o.toString(),type);
//        }
//        return null;
//    }


    public static <T> Result getResultFromJson(String jsonStr,Class<T> clazz){
        Result result = new Result();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            result.setRetCode(jsonObject.getInt("retCode"));
            result.setRetMsg(jsonObject.getBoolean("retMsg"));
            if(!jsonObject.isNull("retData")) {
                JSONObject jsonRetData = jsonObject.getJSONObject("retData");
                if (jsonRetData != null) {
                    Log.e("Utils", "jsonRetData=" + jsonRetData);
                    String date;
                    try {
                        date = URLDecoder.decode(jsonRetData.toString(), I.UTF_8);
                        Log.e("Utils", "jsonRetData=" + date);
                        T t = new Gson().fromJson(date, clazz);
                        result.setRetData(t);
                        return result;

                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                        T t = new Gson().fromJson(jsonRetData.toString(), clazz);
                        result.setRetData(t);
                        return result;
                    }
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    public static <T> Result getListResultFromJson(String jsonStr,Class<T> clazz){
        Result result = new Result();
        Log.e("Utils","jsonStr="+jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            result.setRetCode(jsonObject.getInt("retCode"));
            result.setRetMsg(jsonObject.getBoolean("retMsg"));
            if(!jsonObject.isNull("retData")) {
                JSONArray array = jsonObject.getJSONArray("retData");
                if (array != null) {
                    List<T> list = new ArrayList<T>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonGroupAvatar = array.getJSONObject(i);
                        T ga = new Gson().fromJson(jsonGroupAvatar.toString(), clazz);
                        list.add(ga);
                    }
                    result.setRetData(list);
                    return result;
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    public static <T> Result getPageResultFromJson(String jsonStr,Class<T> clazz){
        Result result = new Result();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            result.setRetCode(jsonObject.getInt("retCode"));
            result.setRetMsg(jsonObject.getBoolean("retMsg"));
            if(!jsonObject.isNull("retData")) {
                JSONObject jsonPager = jsonObject.getJSONObject("retData");
                if (jsonPager != null) {
                    Pager pager = new Pager();
                    pager.setCurrentPage(jsonPager.getInt("currentPage"));
                    pager.setMaxRecord(jsonPager.getInt("maxRecord"));
                    JSONArray array = jsonPager.getJSONArray("pageData");
                    List<T> list = new ArrayList<T>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonGroupAvatar = array.getJSONObject(i);
                        T ga = new Gson().fromJson(jsonGroupAvatar.toString(), clazz);
                        list.add(ga);
                    }
                    pager.setPageData(list);
                    result.setRetData(pager);
                    return result;
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
}
