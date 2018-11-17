package cent.news.com.baseframe.core;

import cent.news.com.baseframe.modules.BaseModuleManage;
import cent.news.com.baseframe.modules.methodsProxy.BaseMethods;
import retrofit2.Retrofit;

/**
 * Created by bym on 2018/6/20.
 */

public interface IBaseBind {

    boolean isLogOpen();

    Retrofit getRestAdapter(Retrofit.Builder builder);

    BaseMethods getMethodInterceptor(BaseMethods.Builder builder);

    BaseModuleManage getModuleManage();

    IBaseBind DEFAULT_BIND = new IBaseBind() {
        @Override
        public boolean isLogOpen() {
            return true;
        }

        @Override
        public Retrofit getRestAdapter(Retrofit.Builder builder) {
            builder.baseUrl("https://github.com/wzqhbkjdx/framework");
            return builder.build();
        }

        @Override
        public BaseMethods getMethodInterceptor(BaseMethods.Builder builder) {
            return builder.build();
        }

        @Override
        public BaseModuleManage getModuleManage() {
            return new BaseModuleManage();
        }
    };


}
