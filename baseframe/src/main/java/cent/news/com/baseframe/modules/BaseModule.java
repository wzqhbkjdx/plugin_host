package cent.news.com.baseframe.modules;

import android.app.Application;

import javax.inject.Singleton;

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
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by bym on 2018/6/20.
 */

@Module
public class BaseModule {

    Application application;

    public BaseModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public Retrofit.Builder provideRetrofit() {
        return new Retrofit.Builder();
    }

    /**
     * 方法编辑器
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseMethods.Builder provideSKYMethods() {
        return new BaseMethods.Builder();
    }

    /**
     * 缓存管理器
     *
     * @return 返回值
     */
    @Provides @Singleton public CacheManager provideCacheManager() {
        return new CacheManager();
    }

    /**
     * Activity堆栈管理
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseScreenManager provideSKYScreenManager() {
        return new BaseScreenManager();
    }

    /**
     * 线程池管理
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseThreadPoolManager provideSKYThreadPoolManager() {
        return new BaseThreadPoolManager();
    }

    /**
     * 结构管理器
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseStructureManage provideSKYStructureManage() {
        return new BaseStructureManage();
    }

    /**
     * 主线程
     *
     * @return 返回值
     */
    @Provides @Singleton public SynchronousExecutor provideSynchronousExecutor() {
        return new SynchronousExecutor();
    }

    /**
     * 提示信息
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseToast provideSKYToast() {
        return new BaseToast();
    }


    /**
     * 下载管理
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseDownloadManager provideSKYDownloadManager() {
        return new BaseDownloadManager();
    }

    /**
     * 文件缓存管理器
     *
     * @return 返回值
     */
    @Provides @Singleton public BaseFileCacheManage provideSKYFileCacheManage() {
        return new BaseFileCacheManage();
    }

    /**
     * 文件缓存管理器
     *
     * @return 返回值
     */
    @Provides @Singleton public InnerJobService provideSKYJobServiceManage() {
        return new InnerJobService();
    }


}
