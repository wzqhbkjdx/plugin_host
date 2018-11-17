package cent.news.com.baseframe.core.plugin;

import cent.news.com.baseframe.core.BaseBiz;

/**
 * Created by bym on 2018/6/19.
 */

public interface BaseBizErrorInterceptor {

    void interceptorError(BaseBiz skyBiz, Throwable throwable);

}
