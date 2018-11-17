package cent.news.com.baseframe.modules.methodsProxy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseProxy {

    public Object impl;                                // 实现类

    public Object proxy;                                // 代理类

    public ConcurrentHashMap<String, BaseMethod> methodCache = new ConcurrentHashMap();    // 方法缓存


    /**
     * 清空
     */
    public void clearProxy() {
        impl = null;
        proxy = null;
        methodCache.clear();
        methodCache = null;
    }


}
