package cent.news.com.baseframe.modules.download;

import android.net.Uri;

/**
 * Created by bym on 2018/6/20.
 */

public class BaseDownloadRequest extends BaseRequest {

    /**
     * 初始化
     *
     * @param uri
     *            地址
     */
    public BaseDownloadRequest(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("下载地址只能是  HTTP/HTTPS 开头！ uri : " + uri);
        }
        setDownloadState(BaseDownloadManager.STATUS_PENDING);
        downloadUrl = uri;
    }

    /**
     * 下载事件
     */
    private BaseDownloadListener baseDownloadListener;

    /**
     * 下载后的文件路径名
     */
    private Uri					downloadUrl;

    /**
     * 下载URL
     */

    private Uri					destinationUrl;

    /**
     * 获取下载事件
     *
     * @return 事件
     */
    public BaseDownloadListener getBaseDownloadListener() {
        return baseDownloadListener;
    }

    /**
     * 下载地址
     *
     * @return 地址
     */
    public Uri getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * 下载后目标地址
     *
     * @return 地址
     */
    public Uri getDestinationUrl() {
        return destinationUrl;
    }

    /**
     * 设置目标地址
     *
     * @param destinationUrl
     *            参数
     * @return 返回值
     */
    public BaseDownloadRequest setDestinationUrl(Uri destinationUrl) {
        this.destinationUrl = destinationUrl;
        return this;
    }

    /**
     * 设置下载事件
     *
     * @param baseDownloadListener
     *            事件
     * @return 返回值
     */
    public BaseDownloadRequest setBaseDownloadListener(BaseDownloadListener baseDownloadListener) {
        this.baseDownloadListener = baseDownloadListener;
        return this;
    }
}
