package cent.news.com.baseframe.core;

import cent.news.com.baseframe.exception.BaseHttpException;
import cent.news.com.baseframe.exception.BaseNotUIPointerException;

/**
 * Created by bym on 2018/6/19.
 */

public interface IBaseIntercept {

    boolean interceptHttpError(BaseHttpException sKYHttpException);

    boolean interceptUIError(BaseNotUIPointerException sKYNotUIPointerException);

    boolean interceptBizError(Throwable throwable);


}
