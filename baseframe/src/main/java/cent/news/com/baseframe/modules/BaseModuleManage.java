package cent.news.com.baseframe.modules;

import android.app.Application;

import javax.inject.Inject;

import cent.news.com.baseframe.core.IBaseBind;
import cent.news.com.baseframe.core.IBaseViewCommon;
import cent.news.com.baseframe.core.SynchronousExecutor;
import cent.news.com.baseframe.modules.cache.CacheManager;
import cent.news.com.baseframe.modules.download.BaseDownloadManager;
import cent.news.com.baseframe.modules.file.BaseFileCacheManage;
import cent.news.com.baseframe.modules.methodsProxy.BaseMethods;
import cent.news.com.baseframe.modules.structure.BaseStructureManage;
import cent.news.com.baseframe.modules.threadPool.BaseThreadPoolManager;
import cent.news.com.baseframe.modules.toast.BaseToast;
import cent.news.com.baseframe.screen.BaseScreenManager;
import cent.news.com.baseframe.service.InnerJobService;
import retrofit2.Retrofit;

/**
 * Created by bym on 2018/6/17.
 */

public class BaseModuleManage {

    @Inject public Application application;

    @Inject public BaseScreenManager screenManager;

    @Inject public CacheManager cacheManager;

    @Inject public SynchronousExecutor synchronousExecutor;

    @Inject public BaseThreadPoolManager baseThreadPoolManager;

    @Inject public BaseStructureManage baseStructureManage;

    @Inject public BaseToast baseToast;

    @Inject public BaseFileCacheManage baseFileCacheManage;

    @Inject public BaseMethods.Builder baseMethodsBuilder;

    @Inject public InnerJobService innerJobService;

    @Inject public Retrofit.Builder retrofitBuilder;

    @Inject public BaseDownloadManager baseDownloadManager;

    public IBaseViewCommon baseViewCommon;

    public Retrofit restAdapter;


    public void init(IBaseBind baseBind, IBaseViewCommon baseViewCommon) {
        this.baseViewCommon = baseViewCommon;
        isLog = baseBind.isLogOpen();
        this.restAdapter = baseBind.getRestAdapter(retrofitBuilder);
        this.baseMethods = baseBind.getMethodInterceptor(baseMethodsBuilder);
        initDatabase();
    }

    public BaseMethods baseMethods;

    public boolean isLog;

    public boolean isLog() {
        return isLog;
    }

    public BaseScreenManager getScreenManager() {
        return screenManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public Application getApplication() {
        return application;
    }

    public BaseMethods getBaseMethods() {
        return baseMethods;
    }

    public SynchronousExecutor getSynchronousExecutor() {
        return synchronousExecutor;
    }

    public BaseThreadPoolManager getBaseThreadPoolManager() {
        return baseThreadPoolManager;
    }

    public BaseStructureManage getBaseStructureManage() {
        return baseStructureManage;
    }

    public IBaseViewCommon getBaseViewCommon() {
        return baseViewCommon;
    }

    public Retrofit getRestAdapter() {
        return restAdapter;
    }

    /**
     * 覆盖这个方法，初始化数据库
     */
    public void initDatabase() {

    }
}













