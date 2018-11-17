package cent.news.com.baseframe.modules.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.IBaseCommonBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.modules.log.L;
import cent.news.com.baseframe.modules.methodsProxy.BaseProxy;
import cent.news.com.baseframe.utils.BaseAppUtil;
import cent.news.com.baseframe.utils.BaseCheckUtils;

/**
 * Created by bym on 2018/6/17.
 */

public final class CacheManager implements ICacheManager {

    private final static int							TYPE_HTTP		= 1;						// 网络

    private final static int							TYPE_COMMON		= 2;						// 公共

    private final static int							TYPE_IMPL		= 3;						// 实现

    private final static int							TYPE_DISPLAY	= 4;						// 跳转调度

    private final LoadingCache<Class<?>, Object>		cache;

    private final ConcurrentHashMap<Class<?>, Integer>	keyType			= new ConcurrentHashMap();

    public CacheManager() {
        // CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
        cache = CacheBuilder.newBuilder()
                // 设置并发级别为10，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(10)
                // 设置写缓存后1分钟过期
                .expireAfterAccess(30, TimeUnit.SECONDS)
                // 设置缓存容器的初始容量为10
                .initialCapacity(10)
                // 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(100)
                // 设置要统计缓存的命中率
                .recordStats()
                // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(new CacheLoader<Class<?>, Object>() {

                    @Override public Object load(Class<?> key) throws Exception {
                        BaseCheckUtils.validateServiceInterface(key);

                        int type = keyType.get(key);

                        switch (type) {
                            case TYPE_HTTP:
                                Object http = BaseHelper.httpAdapter().create(key);
                                if (BaseHelper.isLogOpen()) {
                                    L.tag("SkyCacheManager");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Http加载成功:");
                                    stringBuilder.append(key.getName());
                                    L.i(stringBuilder.toString());
                                }
                                return http;
                            case TYPE_IMPL:
                                Object skyImpl = BaseHelper.methodsProxy().createImpl(key, BaseAppUtil.getImplClass(key));
                                if (BaseHelper.isLogOpen()) {
                                    L.tag("SkyCacheManager");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Interface加载成功:");
                                    stringBuilder.append(key.getName());
                                    L.i(stringBuilder.toString());
                                }
                                return skyImpl;
                            case TYPE_COMMON:
                                BaseProxy skyCommon = BaseHelper.methodsProxy().create(key, BaseAppUtil.getImplClass(key));
                                if (BaseHelper.isLogOpen()) {
                                    L.tag("SkyCacheManager");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Common加载成功:");
                                    stringBuilder.append(key.getName());
                                    L.i(stringBuilder.toString());
                                }
                                return skyCommon;
                            case TYPE_DISPLAY:
                                BaseProxy skyDisplay = BaseHelper.methodsProxy().createDisplay(key, BaseAppUtil.getImplClass(key));
                                if (BaseHelper.isLogOpen()) {
                                    L.tag("SkyCacheManager");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Display加载成功:");
                                    stringBuilder.append(key.getName());
                                    L.i(stringBuilder.toString());
                                }
                                return skyDisplay;
                        }
                        return null;
                    }
                });
    }

    @Override public <D extends BaseIDisplay> D display(Class<D> displayClazz) {
        try {
            keyType.put(displayClazz, TYPE_DISPLAY);
            BaseProxy skyProxy = (BaseProxy) cache.get(displayClazz);
            return (D) skyProxy.proxy;
        } catch (ExecutionException e) {
            keyType.remove(displayClazz);
            if (BaseHelper.isLogOpen()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override public <B extends IBaseCommonBiz> B common(Class<B> service) {
        try {
            keyType.put(service, TYPE_COMMON);
            BaseProxy skyProxy = (BaseProxy) cache.get(service);
            return (B) skyProxy.proxy;
        } catch (ExecutionException e) {
            keyType.remove(service);
            if (BaseHelper.isLogOpen()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override public <I> I interfaces(Class<I> implClazz) {
        try {
            keyType.put(implClazz, TYPE_IMPL);
            return (I) cache.get(implClazz);
        } catch (ExecutionException e) {
            keyType.remove(implClazz);
            if (BaseHelper.isLogOpen()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override public <H> H http(Class<H> httpClazz) {
        try {
            keyType.put(httpClazz, TYPE_HTTP);
            return (H) cache.get(httpClazz);
        } catch (ExecutionException e) {
            keyType.remove(httpClazz);
            if (BaseHelper.isLogOpen()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override public void printState() {
        L.tag("SkyCacheManager");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("命中率:");
        stringBuilder.append(cache.stats().toString());
        L.i(stringBuilder.toString());
    }
}
