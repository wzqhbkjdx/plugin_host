package cent.news.com.baseframe.exception;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseIndexOutOfException extends BaseBizException {

    public BaseIndexOutOfException() {}

    public BaseIndexOutOfException(String detailMessage) {
        super(detailMessage);
    }

    public BaseIndexOutOfException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public BaseIndexOutOfException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
