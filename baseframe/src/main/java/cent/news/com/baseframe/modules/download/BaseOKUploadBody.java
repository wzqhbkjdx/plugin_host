package cent.news.com.baseframe.modules.download;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by bym on 2018/6/21.
 */

public class BaseOKUploadBody extends RequestBody {

    private static final int		SEGMENT_SIZE	= 2048;	// okio.Segment.SIZE

    private final File file;					// 上传的问题件

    private final BaseUploadListener	listener;				// 事件

    private long					totalSize;				// 待上传文件总大小

    private final BaseUploadRequest	SKYUploadRequest;		// 请求

    public BaseOKUploadBody(BaseUploadRequest baseUploadRequest, BaseUploadListener listener) {
        this.file = baseUploadRequest.getSKYUploadBody().file;
        if (file != null) {
            totalSize = file.length();
        }
        this.SKYUploadRequest = baseUploadRequest;
        this.listener = listener;
    }

    @Override public long contentLength() {
        return totalSize;
    }

    @Override public MediaType contentType() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(SKYUploadRequest.getSKYContentType().getMimeType());
        if (SKYUploadRequest.getSKYContentType().getCharset() != null) {
            buffer.append("; charset=");
            buffer.append(SKYUploadRequest.getSKYContentType().getCharset());
        }
        return MediaType.parse(buffer.toString());
    }

    /**
     * @param sink
     *            参数
     * @throws IOException
     *             异常
     */
    @Override public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {

            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                if (SKYUploadRequest.isCanceled()) {
                    //L.i("取消的请求Id " + SKYUploadRequest.getRequestId());
                    return;
                }
                total += read;
                sink.flush();
                int progres = (int) (100 * total / totalSize);
                this.listener.onUploadProgress(SKYUploadRequest.getRequestId(), totalSize, total, progres);// 进度
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

    /**
     * 建造
     *
     * @return 返回值
     */
    public RequestBody build() {
        // 请求头信息
        Headers headers = SKYUploadRequest.getSKYUploadBody().getHeader();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            multipartBuilder.addPart(headers, this);
        }
        List<BaseFormData> SKYHeadersBeans = SKYUploadRequest.getSKYUploadBody().baseFormData;
        if (SKYHeadersBeans != null && SKYHeadersBeans.size() > 0) {
            int count = SKYUploadRequest.getSKYUploadBody().baseFormData.size();
            for (int i = 0; i < count; i++) {
                multipartBuilder.addFormDataPart(SKYHeadersBeans.get(i).key, SKYHeadersBeans.get(i).value);
            }
        }
        RequestBody requestBody = multipartBuilder.build();
        return requestBody;
    }

}
