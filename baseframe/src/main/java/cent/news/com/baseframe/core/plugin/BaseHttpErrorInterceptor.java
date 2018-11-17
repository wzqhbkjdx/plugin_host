package cent.news.com.baseframe.core.plugin;

import cent.news.com.baseframe.core.BaseBiz;
import cent.news.com.baseframe.exception.BaseHttpException;

/**
 * Created by bym on 2018/6/19.
 */

public interface BaseHttpErrorInterceptor {

    void methodError(BaseBiz skyBiz, BaseHttpException skyHttpException);


}
