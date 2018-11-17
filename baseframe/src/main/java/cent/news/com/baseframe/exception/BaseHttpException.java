package cent.news.com.baseframe.exception;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseHttpException extends RuntimeException {

    public BaseHttpException() {}

    public BaseHttpException(String detailMessage) {
        super(detailMessage);
    }

    public BaseHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseHttpException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
