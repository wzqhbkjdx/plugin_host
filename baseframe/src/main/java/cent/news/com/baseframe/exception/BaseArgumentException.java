package cent.news.com.baseframe.exception;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseArgumentException extends BaseBizException {


    public BaseArgumentException() {}

    public BaseArgumentException(String detailMessage) {
        super(detailMessage);
    }

    public BaseArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseArgumentException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }

}
