package cent.news.com.baseframe.modules.download;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cent.news.com.baseframe.BaseHelper;

/**
 * Created by bym on 2018/6/20.
 */

public class BaseDownloadManager implements IBaseDownloadManage {

    private BaseDownloadRequest		SKYDownloadRequest;

    private BaseUploadRequest		SKYUploadRequest;

    private BaseDownloadRequestQueue	mRequestQueue;

    public BaseDownloadManager() {
        mRequestQueue = new BaseDownloadRequestQueue();
        mRequestQueue.start();
    }

    public BaseDownloadManager(int threadPoolSize) {
        mRequestQueue = new BaseDownloadRequestQueue(threadPoolSize);
        mRequestQueue.start();
    }

    /**
     * 添加指令
     *
     * @param request
     *            请求指令
     * @return 返回值
     */
    @Override public int add(BaseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        return mRequestQueue.add(request);
    }

    /**
     * 取消指令
     *
     * @param downloadId
     *            请求ID
     * @return 返回值
     */
    @Override public int cancel(int downloadId) {
        return mRequestQueue.cancel(downloadId);
    }

    /**
     * 取消所有指令
     */
    @Override public void cancelAll() {
        mRequestQueue.cancelAll();
    }

    /**
     * 查询下载状态
     *
     * @param downloadId
     *            请求ID
     * @return 返回值
     */
    @Override public int query(int downloadId) {
        return mRequestQueue.query(downloadId);
    }

    /**
     * 取消所有待决运行的请求，并释放所有的工作线程
     */
    @Override public void release() {
        if (mRequestQueue != null) {
            mRequestQueue.release();
            mRequestQueue = null;
        }
    }

    /**
     * 下载 默认路径 /storage/emulated/0/Android/data/APP包
     *
     * @param url
     *            下载地址
     * @param fileName
     *            文件名
     * @param SKYDownloadListener
     *            事件
     */
    @Override public int download(String url, String fileName, BaseDownloadListener SKYDownloadListener) {
        return download(url, BaseHelper.getInstance().getExternalCacheDir().toString(), fileName, SKYDownloadListener);
    }

    /**
     * 下载
     *
     * @param url
     *            下载地址
     * @param destination
     *            路径
     * @param fileName
     *            文件名
     * @param SKYDownloadListener
     *            事件
     */
    @Override public int download(String url, String destination, String fileName, BaseDownloadListener SKYDownloadListener) {
        StringBuilder sDestination = new StringBuilder(destination);
        StringBuilder sFileName = new StringBuilder(fileName);

        if (destination.endsWith("/")) {
            // 删除斜线 - 防止双斜线
            sDestination.deleteCharAt(destination.length() - 1);
        }

        if (fileName.startsWith("/")) {
            // 删除斜线 - 防止双斜线
            sFileName.deleteCharAt(0);
        }

        Uri uri = Uri.parse(url); // 下载地址
        StringBuilder stringBuilder = new StringBuilder(sDestination.toString());
        stringBuilder.append("/");
        stringBuilder.append(sFileName.toString());
        Uri destinationUri = Uri.parse(stringBuilder.toString());// 目标地址
        return download(uri, destinationUri, SKYDownloadListener);
    }

    /**
     * 下载
     *
     * @param downloadUri
     *            下载地址
     * @param destinationUri
     *            路径
     * @param SKYDownloadListener
     *            事件
     */
    @Override public int download(Uri downloadUri, Uri destinationUri, BaseDownloadListener SKYDownloadListener) {
        SKYDownloadRequest = new BaseDownloadRequest(downloadUri);
        SKYDownloadRequest.setDestinationUrl(destinationUri);
        SKYDownloadRequest.setBaseDownloadListener(SKYDownloadListener);
        return add(SKYDownloadRequest);
    }


    @Override public int upload(Uri uploadUri, BaseUploadHeader baseUploadHeader, BaseUploadBody SKYUploadBody, BaseUploadListener SKYUploadListener) {
        List<BaseUploadHeader> SKYUploadHeaders = new ArrayList<>();
        if (baseUploadHeader != null) {
            SKYUploadHeaders.add(baseUploadHeader);
        }
        return upload(uploadUri, SKYUploadHeaders, SKYUploadBody, BaseContentType.DEFAULT_FILE, SKYUploadListener);
    }

    /**
     * 上传
     *
     * @param uploadUri
     *            上传地址
     * @param SKYUploadHeaders
     *            上传头信息
     * @param SKYUploadBody
     *            上传体
     * @param SKYContentType
     *            类型
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    @Override public int upload(Uri uploadUri, List<BaseUploadHeader> SKYUploadHeaders, BaseUploadBody SKYUploadBody, BaseContentType SKYContentType, BaseUploadListener SKYUploadListener) {
        SKYUploadRequest = new BaseUploadRequest(uploadUri, SKYUploadBody, SKYContentType);
        SKYUploadRequest.setSKYUploadListener(SKYUploadListener);
        for (BaseUploadHeader SKYUploadHeader : SKYUploadHeaders) {
            SKYUploadRequest.addHeader(SKYUploadHeader.headerName, SKYUploadHeader.headerValue);
        }
        return add(SKYUploadRequest);
    }

    /**
     * 上传
     *
     * @param uploadUri
     *            上传地址
     * @param file
     *            文件
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    @Override public int upload(String uploadUri, File file, BaseUploadListener SKYUploadListener) {
        return upload(uploadUri, file, null, SKYUploadListener);
    }

    /**
     * 上传
     *
     * @param uploadUri
     *            上传地址
     * @param file
     *            文件
     * @param SKYUploadHeader
     *            请求头信息
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    @Override public int upload(String uploadUri, File file, BaseUploadHeader SKYUploadHeader, BaseUploadListener SKYUploadListener) {
        List<BaseUploadHeader> SKYUploadHeaders = new ArrayList<>();
        if (SKYUploadHeader != null) {
            SKYUploadHeaders.add(SKYUploadHeader);
        }
        return upload(uploadUri, SKYUploadHeaders, file, SKYUploadListener);
    }

    /**
     * 上传
     *
     * @param uploadUri
     *            上传地址
     * @param SKYUploadHeaders
     *            请求头信息 数组
     * @param file
     *            文件
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    @Override public int upload(String uploadUri, List<BaseUploadHeader> SKYUploadHeaders, File file, BaseUploadListener SKYUploadListener) {
        return upload(uploadUri, SKYUploadHeaders, file, BaseContentType.DEFAULT_FILE, SKYUploadListener);
    }

    /**
     * 上传
     *
     * @param uploadUri
     *            上传地址
     * @param SKYUploadHeaders
     *            请求头信息 数组
     * @param file
     *            文件
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    @Override public int upload(String uploadUri, List<BaseUploadHeader> SKYUploadHeaders, File file, BaseContentType SKYContentType, BaseUploadListener SKYUploadListener) {
        Uri uri = Uri.parse(uploadUri);
        BaseUploadBody SKYUploadBody = new BaseUploadBody();
        SKYUploadBody.headerName = SKYUploadBody.CONTENT_DISPOSITION;
        SKYUploadBody.headerValue = "file";
        SKYUploadBody.file = file;

        return upload(uri, SKYUploadHeaders, SKYUploadBody, SKYContentType, SKYUploadListener);
    }

    @Override public int upload(String uploadUri, List<BaseUploadHeader> SKYUploadHeaders, BaseUploadBody SKYUploadBody, BaseContentType SKYContentType, BaseUploadListener SKYUploadListener) {
        Uri uri = Uri.parse(uploadUri);
        return upload(uri, SKYUploadHeaders, SKYUploadBody, SKYContentType, SKYUploadListener);
    }
}
