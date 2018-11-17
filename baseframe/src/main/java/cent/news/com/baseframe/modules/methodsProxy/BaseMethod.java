package cent.news.com.baseframe.modules.methodsProxy;

import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.BaseBiz;
import cent.news.com.baseframe.core.BaseRunnable;
import cent.news.com.baseframe.core.IBaseIntercept;
import cent.news.com.baseframe.core.plugin.BaseBizErrorInterceptor;
import cent.news.com.baseframe.core.plugin.BaseHttpErrorInterceptor;
import cent.news.com.baseframe.core.plugin.BizEndInterceptor;
import cent.news.com.baseframe.core.plugin.BizStartInterceptor;
import cent.news.com.baseframe.exception.BaseHttpException;
import cent.news.com.baseframe.exception.BaseNotUIPointerException;
import sky.Background;
import sky.BackgroundType;
import sky.Interceptor;
import sky.Repeat;

/**
 * Created by bym on 2018/6/19.
 */

public final class BaseMethod {

    // 执行方法
    public static final int	TYPE_INVOKE_EXE							= 0;

    public static final int	TYPE_DISPLAY_INVOKE_EXE					= 4;

    // 执行后台方法
    public static final int	TYPE_INVOKE_BACKGROUD_HTTP_EXE			= 1;

    public static final int	TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE	= 2;

    public static final int	TYPE_INVOKE_BACKGROUD_WORK_EXE			= 3;

    int				type;

    Object			impl;

    String			implName;

    boolean			isRepeat;

    Method			method;

    MethodRunnable	methodRunnable;

    Class			service;

    int				interceptor;

    Object			backgroundResult;

    boolean			isExe;


    static <T> BaseMethod createDisplayMethod(Method method, Class<T> service) {
        // 是否重复
        boolean isRepeat = parseRepeat(method);
        // 拦截方法标记
        int interceptor = parseInterceptor(method);
        // 判断是否是子线程
        int type = TYPE_DISPLAY_INVOKE_EXE;

        return new BaseMethod(interceptor, method, type, isRepeat, service);

    }


    static BaseMethod createBizMethod(Method method, Class service) {
        // 是否重复
        boolean isRepeat = parseRepeat(method);
        // 拦截方法标记
        int interceptor = parseInterceptor(method);
        // 判断是否是子线程
        int type = parseBackground(method);

        return new BaseMethod(interceptor, method, type, isRepeat, service);
    }

    public BaseMethod(int interceptor, Method method, int type, boolean isRepeat, Class service) {
        this.interceptor = interceptor;
        this.type = type;
        this.isRepeat = isRepeat;
        this.method = method;
        this.service = service;
        if (type == TYPE_INVOKE_BACKGROUD_HTTP_EXE || type == TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE || type == TYPE_INVOKE_BACKGROUD_WORK_EXE) {
            this.methodRunnable = new MethodRunnable();
        }
    }

    private static int parseInterceptor(Method method) {
        // 拦截方法标记
        Interceptor interceptorClass = method.getAnnotation(Interceptor.class);
        if (interceptorClass != null) {
            return interceptorClass.value();
        } else {
            return 0;
        }
    }

    private static int parseBackground(Method method) {
        int type = TYPE_INVOKE_EXE;
        Background background = method.getAnnotation(Background.class);

        if (background != null) {
            BackgroundType backgroundType = background.value();

            switch (backgroundType) {
                case HTTP:
                    type = TYPE_INVOKE_BACKGROUD_HTTP_EXE;
                    break;
                case SINGLEWORK:
                    type = TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE;
                    break;
                case WORK:
                    type = TYPE_INVOKE_BACKGROUD_WORK_EXE;
                    break;
            }
        }

        return type;
    }

    public <T> T invoke(final Object impl, final Object[] args) throws InterruptedException {
        T result = null;
        if (!isRepeat) {
            if (isExe) { // 如果存在什么都不做
                if (BaseHelper.isLogOpen()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(impl.getClass().getSimpleName());
                    stringBuilder.append(".");
                    stringBuilder.append(method.getName());
                }
                return result;
            }
            isExe = true;
        }
        this.impl = impl;
        this.implName = impl.getClass().getName();

        switch (type) {
            case TYPE_INVOKE_EXE:
                defaultMethod(args);
                result = (T) backgroundResult;
                break;
            case TYPE_DISPLAY_INVOKE_EXE:
                displayMethod(args);
                result = (T) backgroundResult;
                break;
            default:
                if (isRepeat) {
                    methodRunnable = new MethodRunnable();
                }
                methodRunnable.setArgs(args);
                switch (type) {
                    case TYPE_INVOKE_BACKGROUD_HTTP_EXE:
                        BaseHelper.threadPoolHelper().getHttpExecutorService().execute(methodRunnable);
                        break;
                    case TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE:
                        BaseHelper.threadPoolHelper().getSingleWorkExecutorService().execute(methodRunnable);
                        break;
                    case TYPE_INVOKE_BACKGROUD_WORK_EXE:
                        BaseHelper.threadPoolHelper().getWorkExecutorService().execute(methodRunnable);
                        break;
                }
                break;
        }

        return result;
    }


    private static boolean parseRepeat(Method method) {

        Repeat SKYRepeat = method.getAnnotation(Repeat.class);
        if (SKYRepeat != null && SKYRepeat.value()) {
            return true;
        } else {
            return false;
        }
    }

    private class MethodRunnable extends BaseRunnable {

        Object[] objects;

        public MethodRunnable() {
            super("MethodRunnable");
        }

        public void setArgs(Object[] objects) {
            this.objects = objects;
        }

        @Override protected void execute() {
            defaultMethod(objects);
        }
    }

    private void displayMethod(Object[] objects) {
        try {
            exeDisplayMethod(method, impl, objects);
        } catch (Throwable throwable) {
            exeError(throwable);
        } finally {
            isExe = false;
        }
    }

    private void defaultMethod(Object[] objects) {
        try {
            exeMethod(method, impl, objects);
        } catch (Throwable throwable) {
            exeError(throwable);
        } finally {
            isExe = false;
        }
    }

    private void exeDisplayMethod(final Method method, final Object impl, final Object[] objects) throws InvocationTargetException, IllegalAccessException {
        boolean isExe = true;
        String clazzName = null;
        Bundle bundle = null;
        // 业务拦截器 - 前
        if (BaseHelper.methodsProxy().displayStartInterceptor != null) {
            String name = method.getName();
            if (name.startsWith("intent") && objects != null) {
                for (Object item : objects) {
                    if (item instanceof Class) {
                        clazzName = ((Class) item).getName();
                    } else if (item instanceof Intent) {
                        clazzName = ((Intent) item).getComponent().getClassName();
                        bundle = ((Intent) item).getExtras();
                    } else if (item instanceof Bundle) {
                        bundle = (Bundle) item;
                    }
                }
            }

            isExe = BaseHelper.methodsProxy().displayStartInterceptor.interceptStart(implName, service, method, interceptor, clazzName, bundle);
        }

        if (isExe) {
            // 如果是主线程 - 直接执行
            if (!BaseHelper.isMainLooperThread()) { // 主线程
                backgroundResult = method.invoke(impl, objects);
                return;
            }
            Runnable runnable = new Runnable() {

                @Override public void run() {
                    try {
                        method.invoke(impl, objects);
                    } catch (Exception throwable) {
                        if (BaseHelper.isLogOpen()) {
                            throwable.printStackTrace();
                        }
                        return;
                    }
                }
            };
            BaseHelper.mainLooper().execute(runnable);
            backgroundResult = null;// 执行
            // 业务拦截器 - 后
            if (BaseHelper.methodsProxy().displayEndInterceptor != null) {
                BaseHelper.methodsProxy().displayEndInterceptor.interceptEnd(implName, service, method, interceptor, clazzName, bundle, backgroundResult);
            }
        } else {
            if (BaseHelper.isLogOpen()) {
                Object[] parameterValues = objects;
                StringBuilder builder = new StringBuilder("\u21E2 ");
                builder.append(method.getName()).append('(');
                if (parameterValues != null) {
                    for (int i = 0; i < parameterValues.length; i++) {
                        if (i > 0) {
                            builder.append(", ");
                        }
                        builder.append(BaseStrings.toString(parameterValues[i]));
                    }
                }

                builder.append(')');
                //L.i("该方法被过滤 - %s", builder.toString());
            }
        }
    }


    public void exeError(Throwable throwable) {
        if (BaseHelper.isLogOpen()) {
            throwable.printStackTrace();
        }

        if(impl instanceof IBaseIntercept){
            IBaseIntercept skyiIntercept = (IBaseIntercept) impl;

            if (throwable.getCause() instanceof BaseHttpException) {
                if (!skyiIntercept.interceptHttpError((BaseHttpException) throwable.getCause())) {
                    // 网络错误拦截器
                    for (BaseHttpErrorInterceptor item : BaseHelper.methodsProxy().skyHttpErrorInterceptors) {
                        item.methodError((BaseBiz) impl, (BaseHttpException) throwable.getCause());
                    }
                }
            } else if (throwable.getCause() instanceof BaseNotUIPointerException) {
                // 忽略
                if (!skyiIntercept.interceptUIError((BaseNotUIPointerException) throwable.getCause())) {
                }
                return;
            } else {
                if (!skyiIntercept.interceptBizError(throwable.getCause())) {
                    // 业务错误拦截器
                    for (BaseBizErrorInterceptor item : BaseHelper.methodsProxy().skyErrorInterceptor) {
                        item.interceptorError((BaseBiz) impl, throwable);
                    }
                }
            }
        }
    }

    public void exeMethod(Method method, Object impl, Object[] objects) throws InvocationTargetException, IllegalAccessException {
        // 业务拦截器 - 前
        for (BizStartInterceptor item : BaseHelper.methodsProxy().bizStartInterceptor) {
            item.interceptStart(implName, service, method, interceptor, objects);
        }
        backgroundResult = method.invoke(impl, objects);// 执行
        // 业务拦截器 - 后
        for (BizEndInterceptor item : BaseHelper.methodsProxy().bizEndInterceptor) {
            item.interceptEnd(implName, service, method, interceptor, objects, backgroundResult);
        }
    }


}




















