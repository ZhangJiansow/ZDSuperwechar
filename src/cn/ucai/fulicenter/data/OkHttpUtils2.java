package cn.ucai.fulicenter.data;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;

/**
 *  * OkHttp框架的二次封装
 * 具有以下功能：
 * 1、下载解析json数据，可根据指定的类型自动转换为相应的实体类或数组
 * 2、上传文件
 * 3、下载文件，当下载文件直接由服务端读写，则可以实现更新下载进度的效果。
 * 没有图片下载、二级缓存的功能。
 *
 * Created by yao on 2016/5/16.
 * 1.增加两个重载的downloadFile方法，一个用于更新下载进度，一个不更新下载进度。
 * 2.在initHandler方法中增加对下载状态的响应。
 * 3.在execute方法中增加对url中request的检查。
 */
public class OkHttpUtils2<T> {
    private static String UTF_8 = "utf-8";
    /** 解析成功的消息 */
    private static final int RESULT_SUCCESS = 0;
    /** 解析失败的消息 */
    private static final int RESULT_ERROR = 1;

    /**下载进度的消息 */
    public static final int DOWNLOADING_PERCENT = 2;
    /** 下载开始的消息 */
    public static final int DOWNLOADING_START = 3;
    /** 下载结束的消息 */
    public static final int DOWNLOADING_FINISH = 4;

    /** mokHttpClient必须保持单例，这样能在一个队列里执行多个请求。
     * 但在Activity等组件的onDestroy方法中要调用close方法释放mOkHttpClient引用的对象 */
    private static OkHttpClient mOkHttpClient;
    /** 工作线程与主线程通信*/
    Handler mHandler;

    /** 解析的目标类对象 */
    Class<T> mClazz;

    /** 上传文件 */
    RequestBody mFileBody;

    /** 在工作线程中下载文件 */
    Callback mCallback;

    /** 保存主线程处理结果的代码*/
    OnCompleteListener<T> mListener;

    /** 用于存放服务端根地址和请求参数 */
    private StringBuilder mUrl;

    /**
     * 主线程执行的代码块，用于处理工作线程的结果
     * @param <T>
     */
    public interface OnCompleteListener<T> {
        /**处理成功返回结果*/
        void onSuccess(T result);

        /**处理返回失败的结果*/
        void onError(String error);
    }

    /**执行本构造方法后，别忘了在当前组件的onDestroy方法中调用
     * OkHttpUtils.release()
     */
    public OkHttpUtils2() {
        if (mOkHttpClient == null) {//mOkHttpClient单例
            mOkHttpClient = new OkHttpClient();
        }
        initHandler();
    }

    /**初始化mHandler*/
    private void initHandler() {
        mHandler = new Handler(SuperWeChatApplication.applicationContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RESULT_SUCCESS:

                        T obj = (T) msg.obj;//获得解析的结果
                        if(mListener!=null) {
                            mListener.onSuccess(obj);//回调解析成功的代码
                        }
                        break;
                    case RESULT_ERROR://
                        if(mListener!=null) {
                            Log.e("main","obj="+msg);
                            mListener.onError(msg.obj.toString());//回调解析失败的代码
                        }
                        break;
                    case DOWNLOADING_START:
                    case DOWNLOADING_FINISH:
                    case DOWNLOADING_PERCENT:
                        if(mListener!=null) {
                            mListener.onSuccess((T) msg);//回调下载开始、结束和更新进度的代码
                        }
                        break;
                }
            }
        };
    }

    /**发送请求，
     * @param listener:保存处理返回结果的代码
     */
    public void execute(OnCompleteListener<T> listener) {
        if (listener != null) {
            mListener = listener;
        }
        if (mClazz == null && mCallback == null) {
            Message msg = Message.obtain();
            msg.what = RESULT_ERROR;
            msg.obj = "忘记调用targetClass()啦";
            Log.e("main", msg.obj.toString());
            mHandler.sendMessage(msg);
            return;
        }

        Request.Builder builder = new Request.Builder().url(mUrl.toString());
//        Log.e("okhttp","murl="+mUrl);
        Request request;
        if (mFileBody != null) {//上传文件
            request = builder.post(mFileBody).build();
        } else {//非下载文件，包括上传文件和json解析
            request = builder.build();
        }
        //创建请求任务
        Call call = mOkHttpClient.newCall(request);
        if (mCallback != null) {//若是下载文件
            call.enqueue(mCallback);//执行程序员自己写的下载文件的代码
            return;
        }
//        //非下载文件场景，检查url中是否含有request参数
//        if (mUrl.indexOf("request") == -1) {
//            Message msg=Message.obtain();
//            msg.what=RESULT_ERROR;
//            msg.obj = "是否忘记设置request参数";
//            sendMessage(msg);
//        }
        //否则，执行解析服务端返回的json数据的请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message msg = Message.obtain();
                msg.what = RESULT_ERROR;
                msg.obj = e.getMessage();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String text = response.body().string();
                if(mClazz.equals(String.class)){
                    Message msg = Message.obtain();
                    msg.what = RESULT_SUCCESS;
                    msg.obj = text;
                    mHandler.sendMessage(msg);
                }else{
                    Gson gson = new Gson();
                    T obj = gson.fromJson(text, mClazz);
                    Message msg = Message.obtain();
                    msg.what = RESULT_SUCCESS;
                    msg.obj = obj;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 设置解析的目标类堆笑
     *
     * @param clazz
     * @return
     */
    public OkHttpUtils2<T> targetClass(Class<T> clazz) {
        mClazz = clazz;
        return this;
    }

    /**
     * iso8859-1的文本转换为utf-8的文本
     *
     * @param iso
     * @return
     */
    public static String iso2utf8(String iso) {
        try {
            String utf8 = new String(iso.getBytes("iso8859-1"), "utf-8");
            return utf8;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return iso;
    }


    /**获取服务端根地址
     * @param rootUrl:
     */
    public OkHttpUtils2<T> url(String rootUrl) {
        this.mUrl = new StringBuilder(rootUrl);
        return this;
    }

    /**
     * 添加请求参数
     * @param key:请求的键
     * @param value：请求的值
     */
    public OkHttpUtils2<T> addParam(String key, String value) {
        if (mUrl == null) {
            return this;
        }
        try {

            if (mUrl.indexOf("?") == -1) {
                mUrl.append("?").append(key).append("=").append(URLEncoder.encode(value,UTF_8));
            } else {
                mUrl.append("&").append(key).append("=").append(URLEncoder.encode(value,UTF_8));
            }
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }


    public OkHttpUtils2<T> setRequestUrl(String request) {
        mUrl = new StringBuilder(I.SERVER_URL);
        mUrl.append(I.QUESTION).append(I.KEY_REQUEST).append(I.EQU).append(request);
//        Log.e("okhttp","1 murl="+ mUrl.toString());
        return this;
    }

    public OkHttpUtils2<T> addFile2(File file) {
        if (mUrl == null) {
            return this;
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);

        mFileBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition","form-data; name=\"file\";filename=\""+file.getName()+"\""), fileBody)
                .build();
        return this;
    }

    public OkHttpUtils2<T> addFile(File file) {
        if (mUrl == null) {
            return this;
        }
        mFileBody = RequestBody.create(null, file);
        return this;
    }

    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     *  设置工作线程中执行的代码，如文件下载。
     *  用于代替缺省的下载解析json的execute中的Callback
     * @param callback:工作线程中执行的代码
     * @return
     */
    public OkHttpUtils2<T> doInBackground(Callback callback) {
        mCallback = callback;
        return this;
    }

    /**工作线程之前，主线程中执行的代码
     * @param runnable:存放主线程中执行的代码块
     */
    public OkHttpUtils2<T> onPreExecute(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        return this;
    }

    /**execute中执行的工作线程中的代码，
     * 调用本方法后，exeucute方法中的参数应设置为null
     * @param listener:保存处理服务端返回结果的代码块
     */
    public OkHttpUtils2<T> onPostExecute(OnCompleteListener<T> listener) {
        mListener = listener;
        return this;
    }

    /**
     * 发送消息，可用于文件下载中的更新下载进度
     * @param msg
     */
    public void sendMessage(Message msg) {
        mHandler.sendMessage(msg);
    }

    public void sendMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }

    /**不更新下载百分比的文件下载
     * @param in
     * @param file
     */
    public void downloadFile(InputStream in, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024 * 5];
            sendMessage(OkHttpUtils2.DOWNLOADING_START);
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            sendMessage(OkHttpUtils2.DOWNLOADING_FINISH);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**本方法在文件下载时，可更新下载百分比
     * @param response:服务端的响应
     * @param file：保存的文件
     */
    public void downloadFile(Response response, File file) {
        FileOutputStream out = null;
        try {
            long fileSize = response.body().contentLength();
            InputStream in = response.body().byteStream();
            out = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024 * 5];
            int total = 0;
            int percent = 1;
            sendMessage(OkHttpUtils2.DOWNLOADING_START);
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                total += len;
                int current = (int) (100L * total / fileSize);
                if (current >= percent) {
                    Message msg = Message.obtain();
                    msg.what = DOWNLOADING_PERCENT;
                    msg.arg1 = current;
                    sendMessage(msg);
                    percent = current + 1;
                }
            }
            sendMessage(OkHttpUtils2.DOWNLOADING_FINISH);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 数组转换为ArrayList集合
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> array2List(T[] array) {
        final List<T> list = Arrays.asList(array);
        return new ArrayList(list);
    }

    /**
     * OkHttpUtils创建后，必须在组件的onDestroy方法中调用本方法，释放mOkHttpClient
     */
    public static void release() {

        if (mOkHttpClient != null) {
            mOkHttpClient = null;
            Log.i("main", "OkHttpClient对象成功释放");
        }
    }

    public void free() {
        mListener=null;
        mHandler=null;
        mFileBody=null;
        mUrl=null;mCallback=null;
        mClazz=null;
    }
}
