package host.news.com.centshost.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import cent.news.com.baseframe.core.IBaseBind;
import cent.news.com.baseframe.modules.BaseModuleManage;
import cent.news.com.baseframe.modules.methodsProxy.BaseMethods;
import host.news.com.centshost.BuildConfig;
import host.news.com.centshost.HostManage.HostModuleManage;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bym on 2018/11/17.
 */

public class HostBind implements IBaseBind {

    @Override
    public boolean isLogOpen() {
        return false;
    }

    @Override
    public Retrofit getRestAdapter(Retrofit.Builder builder) {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();

        // 超时
        okhttpBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(60, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(60, TimeUnit.SECONDS);

        /*@SuppressLint("HardwareIds")
        CommonParamsInterceptor commonParamsInterceptor = new CommonParamsInterceptor.Builder()// 创建
                .addHeaderParam("Accept-Encoding", "gzip, deflate")// 头信息
                //.addQueryParam(NCConstants.KEY_BRAND, android.os.Build.BRAND)
                //.addQueryParam(NCConstants.KEY_MODEL, android.os.Build.MODEL)// 设备模型
                .setOnHeaderParams(new CommonParamsInterceptor.IParams() {
                    @Override
                    public void addParamsMap(Map<String, String> paramsMap) {

                    }
                }).build();

        okhttpBuilder.addInterceptor(commonParamsInterceptor);*/


        switch (BuildConfig.NCBuild) {
            case 0: //测试环境
                LogggingInterceptor interceptor = new LogggingInterceptor();
                interceptor.setLevel(LogggingInterceptor.Level.BODY);
                okhttpBuilder.addInterceptor(interceptor);
                builder.client(okhttpBuilder.build());
                break;
            case 1: //线上环境
                builder.client(okhttpBuilder.build());
                break;
        }

        builder.baseUrl(BuildConfig.NCBuildUrl);

        Gson gson = new GsonBuilder().setLenient().create();
        builder.addConverterFactory(GsonConverterFactory.create(gson));

        return builder.build();
    }

    @Override
    public BaseMethods getMethodInterceptor(BaseMethods.Builder builder) {
        return builder.build();
    }

    @Override
    public BaseModuleManage getModuleManage() {
        return new HostModuleManage();
    }
}
