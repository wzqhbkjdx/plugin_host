package cent.news.com.baseframe;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cent.news.com.baseframe.core.IBaseBind;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.core.IBaseViewCommon;
import cent.news.com.baseframe.core.SynchronousExecutor;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.exception.BaseHttpException;
import cent.news.com.baseframe.modules.BaseModule;
import cent.news.com.baseframe.modules.BaseModuleManage;
import cent.news.com.baseframe.modules.DaggerBaseComponent;
import cent.news.com.baseframe.modules.log.L;
import cent.news.com.baseframe.modules.methodsProxy.BaseMethods;
import cent.news.com.baseframe.modules.structure.IBaseStructureManage;
import cent.news.com.baseframe.modules.threadPool.BaseThreadPoolManager;
import cent.news.com.baseframe.screen.BaseScreenManager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by bym on 2018/6/18.
 */

public class BaseHelper {

    @SuppressLint("StaticFieldLeak")
    private static BaseModuleManage mModulesManage = null;

    public static Bind newBind() {
        return new Bind();
    }

    public static class Bind {
        IBaseBind iBaseBind;

        public Bind setBaseBind(IBaseBind iBaseBind) {
            this.iBaseBind = iBaseBind;
            return this;
        }

        IBaseViewCommon baseViewCommon;

        public Bind setBaseViewCommon(IBaseViewCommon baseViewCommon) {
            this.baseViewCommon = baseViewCommon;
            return this;
        }

        public void inject(Application application) {
            if(application == null) {
                throw new RuntimeException("base framework: Application is not set");
            }

            if(this.iBaseBind == null) {
                this.iBaseBind = IBaseBind.DEFAULT_BIND;
            }

            if(this.baseViewCommon == null) {
                this.baseViewCommon = IBaseViewCommon.BASE_VIEW_COMMON;
            }

            mModulesManage = iBaseBind.getModuleManage();
            if(mModulesManage == null) {
                throw new RuntimeException("base framework: BaseModuleManage is not set");
            }

            DaggerBaseComponent.builder().baseModule(new BaseModule(application)).build().inject(mModulesManage);
            mModulesManage.init(iBaseBind, baseViewCommon);
        }

    }

    protected static <M> M getManage() {
        return (M) mModulesManage;
    }


    public static boolean isLogOpen() {
        return mModulesManage.isLog();
    }

    public static BaseScreenManager screenHelper() {
        return mModulesManage.screenManager;
    }

    /**
     * 页面之间的跳转调度
     * @param eClass
     * @param <D>
     * @return
     */
    public static <D extends BaseIDisplay> D display(Class<D> eClass) {
        return mModulesManage.getCacheManager().display(eClass);
    }

    public static Application getInstance() {
        return mModulesManage.getApplication();
    }

    public static BaseMethods methodsProxy() {
        return mModulesManage.getBaseMethods();
    }

    public static boolean isMainLooperThread() {
        return Looper.getMainLooper().getThread() != Thread.currentThread();
    }

    public static SynchronousExecutor mainLooper() {
        return mModulesManage.getSynchronousExecutor();
    }

    public static BaseThreadPoolManager threadPoolHelper() {
        return mModulesManage.getBaseThreadPoolManager();
    }

    public static IBaseStructureManage structureHelper() {
        return mModulesManage.getBaseStructureManage();
    }

    public static IBaseViewCommon getCommonView() {
        return mModulesManage.getBaseViewCommon();
    }

    public static <B extends IBaseBiz> B biz(Class<B> clazz) {
        return structureHelper().biz(clazz);
    }

    public static <H> H http(Class<H> httpClazz) {
        return mModulesManage.getCacheManager().http(httpClazz);
    }

    public static <I> I interfaces(Class<I> clazz) {
        return mModulesManage.getCacheManager().interfaces(clazz);
    }

    public static Retrofit httpAdapter() {
        return mModulesManage.getRestAdapter();
    }

    public static <D> D httpBody(Call<D> call) {
        if(call == null) {
            throw new BaseHttpException("Call 不能为null");
        }
        Call<D> baseCall;
        if(call.isExecuted()) {
            baseCall = call.clone();
        } else {
            baseCall = call;
        }

        try {
            Response<D> response = baseCall.execute();
            if (!response.isSuccessful()) {
               /* StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("code:");
                stringBuilder.append(response.code());
                stringBuilder.append(" ");
                stringBuilder.append("message:");
                stringBuilder.append(response.message());
                stringBuilder.append(" ");
                stringBuilder.append("errorBody:");
                stringBuilder.append(response.errorBody().string());*/
                throw new BaseHttpException(response.errorBody().string().toString());
            }

            return response.body();
        } catch (IOException e) {
            if (BaseHelper.isLogOpen() && e != null) {
                e.printStackTrace();
                L.i(e.getMessage());
            }
            throw new BaseHttpException("网络异常", e.getCause());
        }
    }
    public static <D> D httpStringBody(Call<D> call) {
        if(call == null) {
            throw new BaseHttpException("Call 不能为null");
        }
        Call<D> baseCall;
        if(call.isExecuted()) {
            baseCall = call.clone();
        } else {
            baseCall = call;
        }

        try {
            Response<D> response = baseCall.execute();
            if (!response.isSuccessful()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("code:");
                stringBuilder.append(response.code());
                stringBuilder.append(" ");
                stringBuilder.append("message:");
                stringBuilder.append(response.message());
                stringBuilder.append(" ");
                stringBuilder.append("errorBody:");
                stringBuilder.append(response.errorBody().string());
                throw new BaseHttpException(stringBuilder.toString());
            }
            StringBuilder stringBuilder =null;
            try {
                stringBuilder = new StringBuilder();
                JSONObject obj = new JSONObject(response.body().toString());
                stringBuilder.append("statusCode:");
                stringBuilder.append(response.code());
                stringBuilder.append(",");
                stringBuilder.append("result:");
                stringBuilder.append(obj.optJSONObject("result").toString());
                stringBuilder.append("}");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return (D) stringBuilder.toString();

        } catch (IOException e) {
            if (BaseHelper.isLogOpen() && e != null) {
                e.printStackTrace();
                L.i(e.getMessage());
            }
            throw new BaseHttpException("网络异常", e.getCause());
        }
    }
    public static void httpCancel(Call call) {
        if(call == null) {
            return;
        }

        if(call.isExecuted()) {
            call.cancel();
        }
    }

}




















