package cent.news.com.baseframe.modules;

import android.app.Application;

import javax.inject.Provider;

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
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;

/**
 * Created by bym on 2018/6/21.
 */

public final class DaggerBaseComponent implements IBaseComponent {

    private Provider<Application> provideApplicationProvider;
    private Provider<CacheManager> provideCacheManagerProvider;

    private Provider<BaseScreenManager> provideSKYScreenManagerProvider;
    private Provider<BaseThreadPoolManager> provideSKYThreadPoolManagerProvider;
    private Provider<BaseStructureManage> provideSKYStructureManageProvider;
    private Provider<SynchronousExecutor> provideSynchronousExecutorProvider;
    private Provider<BaseToast> provideSKYToastProvider;
    private Provider<BaseFileCacheManage> provideSKYFileCacheManageProvider;
    private Provider<BaseMethods.Builder> provideSKYMethodsProvider;
    private Provider<InnerJobService> provideSKYJobServiceManageProvider;
    private Provider<retrofit2.Retrofit.Builder> provideRetrofitProvider;
    private Provider<BaseDownloadManager> provideSKYDownloadManagerProvider;
    private MembersInjector<BaseModuleManage> baseModulesManageMembersInjector;

    private DaggerBaseComponent(DaggerBaseComponent.Builder builder) {
        assert builder != null;
        this.initialize(builder);
    }

    public static DaggerBaseComponent.Builder builder() {
        return new DaggerBaseComponent.Builder();
    }


    private void initialize(DaggerBaseComponent.Builder builder) {
        this.provideApplicationProvider = DoubleCheck.provider(BaseModule_ProvideApplicationFactory.create(builder.baseModule));
        this.provideCacheManagerProvider = DoubleCheck.provider(BaseModule_ProvideCacheManagerFactory.create(builder.baseModule));
        this.provideSKYScreenManagerProvider = DoubleCheck.provider(BaseModule_ProvideSKYScreenManagerFactory.create(builder.baseModule));
        this.provideSKYThreadPoolManagerProvider = DoubleCheck.provider(BaseModule_ProvideSKYThreadPoolManagerFactory.create(builder.baseModule));
        this.provideSKYStructureManageProvider = DoubleCheck.provider(BaseModule_ProvideSKYStructureManageFactory.create(builder.baseModule));
        this.provideSynchronousExecutorProvider = DoubleCheck.provider(BaseModule_ProvideSynchronousExecutorFactory.create(builder.baseModule));
        this.provideSKYToastProvider = DoubleCheck.provider(BaseModule_ProvideSKYToastFactory.create(builder.baseModule));
        this.provideSKYFileCacheManageProvider = DoubleCheck.provider(BaseModule_ProvideSKYFileCacheManageFactory.create(builder.baseModule));
        this.provideSKYMethodsProvider = DoubleCheck.provider(BaseModule_ProvideSKYMethodsFactory.create(builder.baseModule));
        this.provideSKYJobServiceManageProvider = DoubleCheck.provider(BaseModule_ProvideSKYJobServiceManageFactory.create(builder.baseModule));
        this.provideRetrofitProvider = DoubleCheck.provider(BaseModule_ProvideRetrofitFactory.create(builder.baseModule));
        this.provideSKYDownloadManagerProvider = DoubleCheck.provider(BaseModule_ProvideSKYDownloadManagerFactory.create(builder.baseModule));
        this.baseModulesManageMembersInjector = BaseModuleManage_MembersInjector.create(this.provideApplicationProvider, this.provideSKYScreenManagerProvider ,
                this.provideCacheManagerProvider, this.provideSynchronousExecutorProvider,  this.provideSKYThreadPoolManagerProvider,
                this.provideSKYStructureManageProvider, this.provideSKYToastProvider, this.provideSKYFileCacheManageProvider,
                this.provideSKYMethodsProvider, this.provideSKYJobServiceManageProvider, this.provideRetrofitProvider, this.provideSKYDownloadManagerProvider);
    }


    @Override
    public void inject(BaseModuleManage baseModuleManage) {
        this.baseModulesManageMembersInjector.injectMembers(baseModuleManage);
    }

    public static final class Builder {
        private BaseModule baseModule;

        private Builder() {

        }

        public IBaseComponent build() {
            if(this.baseModule == null) {
                throw new IllegalStateException(BaseModule.class.getCanonicalName() + "must be set");
            } else {
                return new DaggerBaseComponent(this);
            }
        }

        public DaggerBaseComponent.Builder baseModule(BaseModule baseModule) {
            this.baseModule = (BaseModule) Preconditions.checkNotNull(baseModule);
            return this;
        }

    }

}


















