package cent.news.com.baseframe.core.plugin;

import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * Created by bym on 2018/6/19.
 */

public interface DisplayEndInterceptor {

    <T> void interceptEnd(String viewName, Class<T> service, Method method, int interceptor, String intent, Bundle bundle, Object backgroundResult);

}
