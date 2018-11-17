package cent.news.com.baseframe.modules.download;

import android.net.Uri;

import cent.news.com.baseframe.utils.BaseCheckUtils;
import okhttp3.Headers;

/**
 * Created by bym on 2018/6/21.
 */

public class BaseUploadRequest extends BaseRequest {

    private Uri uploadUri;

    private BaseUploadListener	SKYUploadListener;

    Headers.Builder				headers;

    BaseContentType				SKYContentType;

    BaseUploadBody				SKYUploadBody;

    /**
     * 初始化
     *
     * @param uri
     *            地址
     * @param SKYUploadBody
     *            请求体
     * @param SKYContentType
     *            类型
     */
    public BaseUploadRequest(Uri uri, BaseUploadBody SKYUploadBody, BaseContentType SKYContentType) {
        if (BaseCheckUtils.isEmpty(SKYUploadBody.headerName) || BaseCheckUtils.isEmpty(SKYUploadBody.headerValue)) {
            throw new IllegalArgumentException("文件体头信息不能为空！");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("上传地址只能是  HTTP/HTTPS 开头！ uri : " + uri);
        }
        setDownloadState(BaseDownloadManager.STATUS_PENDING);
        this.uploadUri = uri;
        this.SKYUploadBody = SKYUploadBody;
        this.headers = new Headers.Builder();
        this.SKYContentType = SKYContentType;
    }

    /**
     * 添加头信息
     *
     * @param headerName
     *            参数
     * @param headerValue
     *            参数
     * @return 返回值
     */
    public BaseUploadRequest addHeader(String headerName, String headerValue) {
        headers.add(headerName, headerValue);
        return this;
    }

    /**
     * 添加头信息
     *
     * @param headerName
     *            参数
     * @param headerValue
     *            参数
     * @return 返回值
     */
    public BaseUploadRequest addHeaderBody(String headerName, String headerValue) {
        headers.add(headerName, headerValue);
        return this;
    }

    /**
     * 返回请求头信息
     *
     * @return 返回值
     */
    public Headers getHeaders() {
        return headers.build();
    }

    /**
     * 返回请求体
     *
     * @return 返回值
     */
    public BaseUploadBody getSKYUploadBody() {
        return SKYUploadBody;
    }

    /**
     * 返回类型
     *
     * @return 返回值
     */
    public BaseContentType getSKYContentType() {
        return SKYContentType;
    }

    /**
     * 上传地址
     *
     * @return 地址
     */
    public Uri getUploadUrl() {
        return uploadUri;
    }

    /**
     * 获取上传事件
     *
     * @return 事件
     */
    public BaseUploadListener getSKYUploadListener() {
        return SKYUploadListener;
    }

    /**
     * 设置下载事件
     *
     * @param SKYUploadListener
     *            事件
     * @return 返回值
     */
    public BaseUploadRequest setSKYUploadListener(BaseUploadListener SKYUploadListener) {
        this.SKYUploadListener = SKYUploadListener;
        return this;
    }


}
