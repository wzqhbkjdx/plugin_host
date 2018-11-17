package cent.news.com.baseframe.exception;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseBizException extends RuntimeException {

    public BaseBizException() {}

    public BaseBizException(String detailMessage) {
        super(detailMessage);
    }

    public BaseBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseBizException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
