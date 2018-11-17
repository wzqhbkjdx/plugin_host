package cent.news.com.baseframe.modules.download;

import android.app.DownloadManager;
import android.os.Process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.modules.log.L;
import cent.news.com.baseframe.utils.BaseAppUtil;
import cent.news.com.baseframe.utils.BaseGsonUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

/**
 * Created by bym on 2018/6/20.
 */

public class SKYDownloadDispatcher extends Thread {

    /** 下载队列请求服务. */
    private final BlockingQueue<BaseRequest> mQueue;

    /** 通知调度死亡 */
    private volatile boolean					mQuit									= false;

    /** 请求 */
    private BaseRequest						mRequest;

    /** 数据流缓冲大小 */
    public final int							BUFFER_SIZE								= 4096;

    /** 记录重定向多少次. */
    private int									mRedirectionCount						= 0;

    /** 最大重定向数量 */
    public final int							MAX_REDIRECTS							= 5;

    /** 网络底层协议 **/
    OkHttpClient okHttpClient;

    /**
     * 网络响应
     */
    private final int							HTTP_REQUESTED_RANGE_NOT_SATISFIABLE	= 416;	// 资源范围

    private final int							HTTP_TEMP_REDIRECT						= 307;	// 重定向

    private long								mContentLength;									// 内容长度

    private long								mCurrentBytes;									// 下载字节

    boolean										shouldAllowRedirects					= true;	// 是否开启重定向

    Gson gson;

    /**
     * 初始化调度器
     *
     * @param queue
     *            队列
     * @param okHttpClient
     *            网路底层协议
     */
    public SKYDownloadDispatcher(BlockingQueue<BaseRequest> queue, OkHttpClient okHttpClient) {
        mQueue = queue;
        this.okHttpClient = okHttpClient;
        gson = new GsonBuilder().create();
    }

    /**
     * 执行
     */
    @Override public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (true) {
            try {
                mRequest = mQueue.take(); // 从队列中取出指令

                if (mRequest instanceof BaseDownloadRequest) { // 下载请求
                    BaseDownloadRequest baseDownloadRequest = (BaseDownloadRequest) mRequest;
                    mRedirectionCount = 0; // 重定向清零
                    //L.i("请求ID = " + SKYDownloadRequest.getRequestId());
                    baseDownloadRequest.setDownloadState(BaseDownloadManager.STATUS_STARTED);// 设置状态
                    executeDownload(baseDownloadRequest, baseDownloadRequest.getDownloadUrl().toString());
                } else if (mRequest instanceof BaseUploadRequest) { // 上传请求
                    BaseUploadRequest baseUploadRequest = (BaseUploadRequest) mRequest;
                    mRedirectionCount = 0; // 重定向清零
                    //L.i("请求ID = " + SKYUploadRequest.getRequestId());
                    baseUploadRequest.setDownloadState(BaseDownloadManager.STATUS_STARTED);// 设置状态
                    executeUpload(baseUploadRequest, baseUploadRequest.getUploadUrl().toString());
                } else {
                    //L.i("未知指令");
                }

            } catch (InterruptedException e) {
                if (mQuit) {
                    if (mRequest instanceof BaseDownloadRequest) { // 下载请求
                        BaseDownloadRequest baseDownloadRequest = (BaseDownloadRequest) mRequest;
                        baseDownloadRequest.finish();
                        updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
                    } else if (mRequest instanceof BaseUploadRequest) { // 上传请求
                        BaseUploadRequest baseUploadRequest = (BaseUploadRequest) mRequest;
                        baseUploadRequest.finish();
                        updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消上传");

                    } else {
                        //L.i("未知指令");
                    }
                    return;
                }
                continue;
            }
        }
    }

    /**
     * *******************************上传****************************************
     */

    /**
     * 执行上传
     *
     * @param baseUploadRequest
     *            下载请求
     * @param uploadUrl
     *            请求url
     */
    private void executeUpload(BaseUploadRequest baseUploadRequest, String uploadUrl) {
        URL url = null;
        try {
            url = new URL(uploadUrl);
        } catch (MalformedURLException e) {
            updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_MALFORMED_URI, "异常 : 不正确的地址");
            return;
        }
        updateUploadState(baseUploadRequest, BaseDownloadManager.STATUS_CONNECTING);
        // 创建文件体
        BaseOKUploadBody baseOkUploadBody = new BaseOKUploadBody(baseUploadRequest, baseUploadRequest.getSKYUploadListener());
        // 请求
        Request request = new Request.Builder().tag(baseUploadRequest.getRequestTag()).url(url).headers(baseUploadRequest.getHeaders()).post(baseOkUploadBody.build()).build();// 创建请求

        try {
            updateUploadState(baseUploadRequest, BaseDownloadManager.STATUS_RUNNING);
            Response response = okHttpClient.newCall(request).execute();
            final int responseCode = response.code();
            //L.i("请求头信息:" + request.headers().toString());
            //L.i("请求编号:" + baseUploadRequest.getRequestId() + ", 响应编号 : " + responseCode);

            switch (responseCode) {
                case HTTP_OK:
                    if (baseUploadRequest.isCanceled()) {
                        baseUploadRequest.finish();
                        updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
                        return;
                    }
                    if (!response.isSuccessful()) {
                        updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_HTTP_DATA_ERROR, "成功响应,但上传失败！");
                        return;
                    }
                    shouldAllowRedirects = false;
                    postUploadComplete(baseUploadRequest, response);// 上传成功
                    return;
                case HTTP_MOVED_PERM:
                case HTTP_MOVED_TEMP:
                case HTTP_SEE_OTHER:
                case HTTP_TEMP_REDIRECT:
                    while (mRedirectionCount++ < MAX_REDIRECTS && shouldAllowRedirects) {
                        L.i("重定向 Id " + baseUploadRequest.getRequestId());
                        final String location = response.header("Location");
                        executeUpload(baseUploadRequest, location); // 执行下载
                        continue;
                    }

                    if (mRedirectionCount > MAX_REDIRECTS) {
                        updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_TOO_MANY_REDIRECTS, "重定向太多，导致下载失败,默认最多 5次重定向！");
                        return;
                    }
                    break;
                case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
                    updateUploadFailed(baseUploadRequest, HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, response.message());
                    break;
                case HTTP_UNAVAILABLE:
                    updateUploadFailed(baseUploadRequest, HTTP_UNAVAILABLE, response.message());
                    break;
                case HTTP_INTERNAL_ERROR:
                    updateUploadFailed(baseUploadRequest, HTTP_INTERNAL_ERROR, response.message());
                    break;
                default:
                    updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_UNHANDLED_HTTP_CODE, "未处理的响应:" + responseCode + " 信息:" + response.message());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateUploadFailed(baseUploadRequest, BaseDownloadManager.ERROR_HTTP_DATA_ERROR, "故障");
        }
    }

    /**
     * 更新状态 - 上传失败
     *
     * @param baseUploadRequest
     *            参数
     * @param errorCode
     *            参数
     * @param errorMsg
     *            参数
     */
    public void updateUploadFailed(BaseUploadRequest baseUploadRequest, int errorCode, String errorMsg) {
        shouldAllowRedirects = false;
        baseUploadRequest.setDownloadState(BaseDownloadManager.STATUS_FAILED);
        if (baseUploadRequest.getSKYUploadListener() != null) {
            postUploadFailed(baseUploadRequest, errorCode, errorMsg);
            baseUploadRequest.finish();
        }
    }

    /**
     * 更新状态 - 链接
     *
     * @param SKYUploadRequest
     *            参数
     * @param state
     *            参数
     */
    public void updateUploadState(BaseUploadRequest SKYUploadRequest, int state) {
        SKYUploadRequest.setDownloadState(state);
    }

    /**
     * 上传失败
     *
     * @param request
     *            请求
     * @param errorCode
     *            错误编号
     * @param errorMsg
     *            错误信息
     */
    public void postUploadFailed(final BaseUploadRequest request, final int errorCode, final String errorMsg) {
        BaseHelper.mainLooper().execute(new Runnable() {

            @Override public void run() {
                request.getSKYUploadListener().onUploadFailed(request.getRequestId(), errorCode, errorMsg);
            }
        });
    }

    /**
     * 上传成功
     *
     * @param request
     *            请求
     * @param response
     *            参数
     */
    public void postUploadComplete(final BaseUploadRequest request, final Response response) {
        try {

            final Class clazz = BaseAppUtil.getSuperClassGenricType(request.getSKYUploadListener().getClass(), 0);

            final Object value = BaseGsonUtils.readBody(gson, Charset.forName("UTF-8"), response.body(), clazz);
            BaseHelper.mainLooper().execute(new Runnable() {

                @Override public void run() {
                    if (clazz == null) {
                        request.getSKYUploadListener().onUploadComplete(request.getRequestId(), response);
                    } else {
                        request.getSKYUploadListener().onUploadComplete(request.getRequestId(), value);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            BaseHelper.mainLooper().execute(new Runnable() {

                public void run() {
                    request.getSKYUploadListener().onUploadFailed(request.getRequestId(), 0, "上传成功-数据转换失败");
                }
            });
        }
    }

    /**
     * *******************************下载****************************************
     */
    /**
     * 执行下载
     *
     *            下载请求
     * @param downloadUrl
     *            请求url
     */
    private void executeDownload(BaseDownloadRequest baseDownloadRequest, String downloadUrl) {

        URL url = null;
        try {
            url = new URL(downloadUrl);
        } catch (MalformedURLException e) {
            updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_MALFORMED_URI, "异常 : 不正确的地址");
            return;
        }

        Request request = new Request.Builder().tag(baseDownloadRequest.getRequestTag()).url(url).build();// 创建请求
        try {
            Response response = okHttpClient.newCall(request).execute();

            updateDownloadState(baseDownloadRequest, BaseDownloadManager.STATUS_CONNECTING);

            final int responseCode = response.code();

            L.i("请求编号:" + baseDownloadRequest.getRequestId() + ", 响应编号 : " + responseCode);

            switch (responseCode) {
                case HTTP_OK:
                    shouldAllowRedirects = false;
                    if (readResponseHeaders(baseDownloadRequest, response) == 1) {
                        transferData(baseDownloadRequest, response);
                    } else {
                        updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_DOWNLOAD_SIZE_UNKNOWN, "服务端没有返回文件长度，长度不确定导致失败！");
                    }
                    return;
                case HTTP_MOVED_PERM:
                case HTTP_MOVED_TEMP:
                case HTTP_SEE_OTHER:
                case HTTP_TEMP_REDIRECT:
                    while (mRedirectionCount++ < MAX_REDIRECTS && shouldAllowRedirects) {
                        //L.i("重定向 Id " + SKYDownloadRequest.getRequestId());
                        final String location = response.header("Location");
                        executeDownload(baseDownloadRequest, location); // 执行下载
                        continue;
                    }

                    if (mRedirectionCount > MAX_REDIRECTS) {
                        updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_TOO_MANY_REDIRECTS, "重定向太多，导致下载失败,默认最多 5次重定向！");
                        return;
                    }
                    break;
                case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
                    updateDownloadFailed(baseDownloadRequest, HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, response.message());
                    break;
                case HTTP_UNAVAILABLE:
                    updateDownloadFailed(baseDownloadRequest, HTTP_UNAVAILABLE, response.message());
                    break;
                case HTTP_INTERNAL_ERROR:
                    updateDownloadFailed(baseDownloadRequest, HTTP_INTERNAL_ERROR, response.message());
                    break;
                default:
                    updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_UNHANDLED_HTTP_CODE, "未处理的响应:" + responseCode + " 信息:" + response.message());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateDownloadFailed(baseDownloadRequest, BaseDownloadManager.ERROR_HTTP_DATA_ERROR, "故障");
        }
    }

    /**
     * 流的读取
     *
     * @param SKYDownloadRequest
     *            参数
     * @param conn
     *            参数
     */
    private void transferData(BaseDownloadRequest SKYDownloadRequest, Response conn) {
        InputStream in = null;
        OutputStream out = null;
        cleanupDestination(SKYDownloadRequest);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            in = conn.body().byteStream();

            File destinationFile = new File(SKYDownloadRequest.getDestinationUrl().getPath().toString());

            try {
                out = new FileOutputStream(destinationFile, true);
            } catch (IOException e) {
                e.printStackTrace();
                updateDownloadFailed(SKYDownloadRequest, BaseDownloadManager.ERROR_FILE_ERROR, "路径转换文件时错误");
            }

            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(out);

            transferData(SKYDownloadRequest, bis, bos);

        } finally {

            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (bos != null) bos.flush();
            } catch (IOException e) {
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 流的读取
     *
     * @param SKYDownloadRequest
     *            参数
     * @param in
     *            参数
     * @param out
     *            参数
     */
    private void transferData(BaseDownloadRequest SKYDownloadRequest, BufferedInputStream in, BufferedOutputStream out) {
        final byte data[] = new byte[BUFFER_SIZE];
        mCurrentBytes = 0;
        SKYDownloadRequest.setDownloadState(BaseDownloadManager.STATUS_RUNNING);
        L.i("内容长度: " + mContentLength + " 下载ID : " + SKYDownloadRequest.getRequestId());
        for (;;) {
            if (SKYDownloadRequest.isCanceled()) {
                L.i("取消的请求Id " + SKYDownloadRequest.getRequestId());
                mRequest.finish();
                updateDownloadFailed(SKYDownloadRequest, BaseDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
                return;
            }
            int bytesRead = readFromResponse(SKYDownloadRequest, data, in);

            if (mContentLength != -1) {
                int progress = (int) ((mCurrentBytes * 100) / mContentLength);
                updateDownloadProgress(SKYDownloadRequest, progress, mCurrentBytes);
            }

            if (bytesRead == -1) {
                updateDownloadComplete(SKYDownloadRequest);
                return;
            } else if (bytesRead == Integer.MIN_VALUE) {
                return;
            }

            writeDataToDestination(SKYDownloadRequest, data, bytesRead, out);
            mCurrentBytes += bytesRead;
        }
    }

    /**
     * 从响应体里读取
     *
     * @param SKYDownloadRequest
     *            参数
     * @param data
     *            参数
     * @param entityStream
     *            参数
     * @return 返回值
     */
    private int readFromResponse(BaseDownloadRequest SKYDownloadRequest, byte[] data, BufferedInputStream entityStream) {
        try {
            return entityStream.read(data);
        } catch (IOException ex) {
            updateDownloadFailed(SKYDownloadRequest, BaseDownloadManager.ERROR_HTTP_DATA_ERROR, "无法读取响应");
            return Integer.MIN_VALUE;
        }
    }

    /**
     * 下载数据到目标文件
     *
     * @param SKYDownloadRequest
     *            参数
     * @param data
     *            参数
     * @param bytesRead
     *            参数
     * @param out
     *            参数
     */
    private void writeDataToDestination(BaseDownloadRequest SKYDownloadRequest, byte[] data, int bytesRead, BufferedOutputStream out) {
        while (true) {
            try {
                out.write(data, 0, bytesRead);
                return;
            } catch (IOException ex) {
                updateDownloadFailed(SKYDownloadRequest, BaseDownloadManager.ERROR_FILE_ERROR, "写入目标文件时，IO异常错误");
            }
        }
    }

    /**
     * 读取响应头信息
     *
     * @param SKYDownloadRequest
     *            参数
     * @param response
     *            参数
     * @return 返回值
     */
    private int readResponseHeaders(BaseDownloadRequest SKYDownloadRequest, Response response) {
        final String transferEncoding = response.header("Transfer-Encoding");

        if (transferEncoding == null) {
            mContentLength = getHeaderFieldLong(response, "Content-Length", -1);
        } else {
            L.i("长度无法确定的请求Id " + SKYDownloadRequest.getRequestId());
            mContentLength = -1;
        }

        if (mContentLength == -1 && (transferEncoding == null || !transferEncoding.equalsIgnoreCase("chunked"))) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 读取头信息
     *
     * @param conn
     *            参数
     * @param field
     *            参数 参数
     * @param defaultValue
     *            参数
     * @return 返回值
     */
    public long getHeaderFieldLong(Response conn, String field, long defaultValue) {
        try {
            return Long.parseLong(conn.header(field));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 清理目标文件
     *
     * @param SKYDownloadRequest
     *            参数
     */
    private void cleanupDestination(BaseDownloadRequest SKYDownloadRequest) {
        //L.i("目标文件路径 : " + SKYDownloadRequest.getDestinationUrl());
        File destinationFile = new File(SKYDownloadRequest.getDestinationUrl().getPath().toString());
        if (destinationFile.exists()) {
            destinationFile.delete();
        }
    }

    /**
     * 更新状态 - 链接
     *
     * @param SKYDownloadRequest
     *            参数
     * @param state
     *            参数
     */
    public void updateDownloadState(BaseDownloadRequest SKYDownloadRequest, int state) {
        SKYDownloadRequest.setDownloadState(state);
    }

    /**
     * 更新状态 - 下载成功
     *
     * @param baseDownloadRequest
     *            参数
     */
    public void updateDownloadComplete(BaseDownloadRequest baseDownloadRequest) {
        baseDownloadRequest.setDownloadState(BaseDownloadManager.STATUS_SUCCESSFUL);
        if (baseDownloadRequest.getBaseDownloadListener() != null) {
            postDownloadComplete(baseDownloadRequest);
            baseDownloadRequest.finish();
        }
    }

    /**
     * 更新状态 - 下载失败
     *
     *            参数
     * @param errorCode
     *            参数
     * @param errorMsg
     *            参数
     */
    public void updateDownloadFailed(BaseDownloadRequest baseDownloadRequest, int errorCode, String errorMsg) {
        shouldAllowRedirects = false;
        baseDownloadRequest.setDownloadState(BaseDownloadManager.STATUS_FAILED);
        cleanupDestination(baseDownloadRequest);
        if (baseDownloadRequest.getBaseDownloadListener() != null) {
            postDownloadFailed(baseDownloadRequest, errorCode, errorMsg);
            baseDownloadRequest.finish();
        }
    }

    /**
     * 更新状态 - 加载进度
     *
     * @param baseDownloadRequest
     *            参数
     * @param progress
     *            参数
     * @param downloadedBytes
     *            参数
     */
    public void updateDownloadProgress(BaseDownloadRequest baseDownloadRequest, int progress, long downloadedBytes) {
        if (baseDownloadRequest.getBaseDownloadListener() != null) {
            postProgressUpdate(baseDownloadRequest, mContentLength, downloadedBytes, progress);
        }
    }

    /**
     * 下载成功
     *
     * @param request
     *            请求
     */
    public void postDownloadComplete(final BaseDownloadRequest request) {
        BaseHelper.mainLooper().execute(new Runnable() {

            @Override public void run() {
                request.getBaseDownloadListener().onDownloadComplete(request.getRequestId());
            }
        });
    }

    /**
     * 下载失败
     *
     * @param request
     *            请求
     * @param errorCode
     *            错误编号
     * @param errorMsg
     *            错误信息
     */
    public void postDownloadFailed(final BaseDownloadRequest request, final int errorCode, final String errorMsg) {
        BaseHelper.mainLooper().execute(new Runnable() {

            @Override public void run() {
                request.getBaseDownloadListener().onDownloadFailed(request.getRequestId(), errorCode, errorMsg);
            }
        });
    }

    /**
     * 下载进度
     *
     * @param request
     *            请求
     * @param totalBytes
     *            总字节数
     * @param downloadedBytes
     *            参数
     * @param progress
     *            参数
     */
    public void postProgressUpdate(final BaseDownloadRequest request, final long totalBytes, final long downloadedBytes, final int progress) {
        BaseHelper.mainLooper().execute(new Runnable() {

            @Override public void run() {
                request.getBaseDownloadListener().onDownloadProgress(request.getRequestId(), totalBytes, downloadedBytes, progress);
            }
        });
    }

    /**
     * *******************************上传****************************************
     */
    /**
     * 退出
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }


}
