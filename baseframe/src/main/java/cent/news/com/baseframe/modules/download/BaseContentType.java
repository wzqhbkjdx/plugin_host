package cent.news.com.baseframe.modules.download;

import java.nio.charset.Charset;

/**
 * Created by bym on 2018/6/20.
 */

public class BaseContentType {

    public static final BaseContentType	APPLICATION_ATOM_XML		= create("application/atom+xml", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	APPLICATION_FORM_URLENCODED	= create("application/x-www-form-urlencoded", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	APPLICATION_JSON			= create("application/json", BaseDownloadConstants.UTF_8);

    public static final BaseContentType	APPLICATION_OCTET_STREAM	= create("application/octet-stream", (Charset) null);

    public static final BaseContentType	APPLICATION_SVG_XML			= create("application/svg+xml", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	APPLICATION_XHTML_XML		= create("application/xhtml+xml", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	APPLICATION_XML				= create("application/xml", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	MULTIPART_FORM_DATA			= create("multipart/form-data", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	TEXT_HTML					= create("text/html", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	TEXT_PLAIN					= create("text/plain", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	TEXT_XML					= create("text/xml", BaseDownloadConstants.ISO_8859_1);

    public static final BaseContentType	IMAGE_PNG					= create("image/png", (Charset) null);

    public static final BaseContentType	IMAGE_JPG					= create("image/jpeg", (Charset) null);

    public static final BaseContentType	WILDCARD					= create("*/*", (Charset) null);

    public static final BaseContentType	DEFAULT_TEXT				= TEXT_PLAIN;

    public static final BaseContentType	DEFAULT_FILE				= MULTIPART_FORM_DATA;

    /**
     * 创建类型
     *
     * @param mimeType
     *            参数
     * @param charset
     *            参数
     * @return 返回值
     */
    public static BaseContentType create(final String mimeType, final Charset charset) {
        return new BaseContentType(mimeType, charset);
    }

    private final String	mimeType;

    private final Charset	charset;

    /**
     * @param mimeType
     *            参数
     * @param charset
     *            参数
     */
    BaseContentType(final String mimeType, final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Charset getCharset() {
        return this.charset;
    }

}
