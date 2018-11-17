package cent.news.com.baseframe.modules.methodsProxy;

import android.os.Build;
import android.os.Looper;
import android.os.Trace;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.plugin.BaseActivityInterceptor;
import cent.news.com.baseframe.core.plugin.BaseBizErrorInterceptor;
import cent.news.com.baseframe.core.plugin.BaseFragmentInterceptor;
import cent.news.com.baseframe.core.plugin.BaseHttpErrorInterceptor;
import cent.news.com.baseframe.core.plugin.BaseLayoutInterceptor;
import cent.news.com.baseframe.core.plugin.BizEndInterceptor;
import cent.news.com.baseframe.core.plugin.BizStartInterceptor;
import cent.news.com.baseframe.core.plugin.DisplayEndInterceptor;
import cent.news.com.baseframe.core.plugin.DisplayStartInterceptor;
import cent.news.com.baseframe.core.plugin.ImplEndInterceptor;
import cent.news.com.baseframe.core.plugin.ImplStartInterceptor;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import sky.cglib.proxy.Enhancer;
import sky.cglib.proxy.MethodInterceptor;

/**
 * Created by bym on 2018/6/18.
 */

public final class BaseMethods {

    final BaseActivityInterceptor baseActivityInterceptor;

    final BaseLayoutInterceptor skyLayoutInterceptor;

    final BaseFragmentInterceptor skyFragmentInterceptor;

    final ArrayList<BizStartInterceptor> bizStartInterceptor;		// 方法开始拦截器

    final DisplayStartInterceptor displayStartInterceptor;	// 方法开始拦截器

    final ArrayList<BizEndInterceptor>			bizEndInterceptor;			// 方法结束拦截器

    final DisplayEndInterceptor displayEndInterceptor;		// 方法结束拦截器

    private ArrayList<ImplStartInterceptor>		implStartInterceptors;		// 方法开始拦截器

    private ArrayList<ImplEndInterceptor>		implEndInterceptors;		// 方法结束拦截器

    final ArrayList<BaseBizErrorInterceptor>		skyErrorInterceptor;		// 方法错误拦截器

    final ArrayList<BaseHttpErrorInterceptor>	skyHttpErrorInterceptors;	// 网络错误拦截器

    public BaseLayoutInterceptor interceptor() {
        return skyLayoutInterceptor;
    }

    public BaseMethods(BaseLayoutInterceptor skyLayoutInterceptor, BaseActivityInterceptor SKYActivityInterceptor, BaseFragmentInterceptor SKYFragmentInterceptor,
                       ArrayList<BizStartInterceptor> bizStartInterceptor, DisplayStartInterceptor displayStartInterceptor, ArrayList<BizEndInterceptor> bizEndInterceptor,
                       DisplayEndInterceptor displayEndInterceptor, ArrayList<ImplStartInterceptor> implStartInterceptors, ArrayList<ImplEndInterceptor> implEndInterceptors,
                       ArrayList<BaseBizErrorInterceptor> SKYErrorInterceptor, ArrayList<BaseHttpErrorInterceptor> skyHttpErrorInterceptors) {
        this.skyLayoutInterceptor = skyLayoutInterceptor;
        this.bizEndInterceptor = bizEndInterceptor;
        this.displayEndInterceptor = displayEndInterceptor;
        this.displayStartInterceptor = displayStartInterceptor;
        this.bizStartInterceptor = bizStartInterceptor;
        this.skyErrorInterceptor = SKYErrorInterceptor;
        this.implStartInterceptors = implStartInterceptors;
        this.implEndInterceptors = implEndInterceptors;
        this.baseActivityInterceptor = SKYActivityInterceptor;
        this.skyFragmentInterceptor = SKYFragmentInterceptor;
        this.skyHttpErrorInterceptors = skyHttpErrorInterceptors;
    }

    public <T> BaseProxy createNotInf(final Class superClazz, Object impl) {
        final BaseProxy BaseProxy = new BaseProxy();
        BaseProxy.impl = impl;
        Enhancer e = new Enhancer(BaseHelper.getInstance());
        e.setSuperclass(superClazz);
        e.setInterceptor(new MethodInterceptor() {

            @Override public Object intercept(String name, Class[] argsType, Object[] args) throws Exception {
                Method method = superClazz.getMethod(name, argsType);

                // 如果有返回值 - 直接执行
                if (!method.getReturnType().equals(void.class)) {
                    return method.invoke(BaseProxy.impl, args);
                }

                BaseMethod baseMethod = loadBaseMethod(BaseProxy, method, superClazz);
                // 开始
                if (!BaseHelper.isLogOpen()) {
                    return baseMethod.invoke(BaseProxy.impl, args);
                }
                enterMethod(method, args);
                long startNanos = System.nanoTime();

                Object result = baseMethod.invoke(BaseProxy.impl, args);

                long stopNanos = System.nanoTime();
                long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
                exitMethod(method, result, lengthMillis);

                return result;
            }
        });
        BaseProxy.proxy = e.create();
        return BaseProxy;
    }


    public <T> BaseProxy create(final Class<T> service, Object impl) {
        BaseCheckUtils.validateServiceInterface(service);

        final BaseProxy BaseProxy = new BaseProxy();
        BaseProxy.impl = impl;
        BaseProxy.proxy = Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new BaseInvocationHandler() {

            @Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                // 如果有返回值 - 直接执行
                if (!method.getReturnType().equals(void.class)) {
                    return method.invoke(BaseProxy.impl, args);
                }

                BaseMethod baseMethod = loadBaseMethod(BaseProxy, method, service);
                // 开始
                if (!BaseHelper.isLogOpen()) {
                    return baseMethod.invoke(BaseProxy.impl, args);
                }
                enterMethod(method, args);
                long startNanos = System.nanoTime();

                Object result = baseMethod.invoke(BaseProxy.impl, args);

                long stopNanos = System.nanoTime();
                long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
                exitMethod(method, result, lengthMillis);

                return result;
            }
        });

        return BaseProxy;
    }


    public <T> BaseProxy createDisplay(final Class<T> service, Object impl) {
        BaseCheckUtils.validateServiceInterface(service);

        final BaseProxy baseProxy = new BaseProxy();
        baseProxy.impl = impl;
        baseProxy.proxy = Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new BaseInvocationHandler() {

            @Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                // 如果有返回值 - 直接执行
                if (!method.getReturnType().equals(void.class)) {
                    return method.invoke(baseProxy.impl, args);

                }
                BaseMethod BaseMethod = loadDisplayBaseMethod(baseProxy, method, service);
                // 开始
                if (!BaseHelper.isLogOpen()) {
                    return BaseMethod.invoke(baseProxy.impl, args);
                }
                enterMethod(method, args);
                long startNanos = System.nanoTime();

                Object result = BaseMethod.invoke(baseProxy.impl, args);

                long stopNanos = System.nanoTime();
                long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
                exitMethod(method, result, lengthMillis);

                return result;
            }
        });

        return baseProxy;
    }



    private <T> BaseMethod loadBaseMethod(BaseProxy baseProxy, Method method, Class<T> service) {
        synchronized (baseProxy.methodCache) {
            String methodKey = getKey(method, method.getParameterTypes());
            BaseMethod baseMethod = baseProxy.methodCache.get(methodKey);
            if (baseMethod == null) {
                baseMethod = BaseMethod.createBizMethod(method, service);
                baseProxy.methodCache.put(methodKey, baseMethod);
            }
            return baseMethod;
        }
    }

    private void exitMethod(Method method, Object result, long lengthMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }
        Class<?> cls = method.getDeclaringClass();
        String methodName = method.getName();
        boolean hasReturnType = method.getReturnType() != void.class;

        StringBuilder builder = new StringBuilder("\u21E0 ").append(methodName).append(" [").append(lengthMillis).append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(BaseStrings.toString(result));
        }
        //Log.v(cls.getSimpleName(), builder.toString());
    }

    /**
     * 获取方法唯一标记
     *
     * @param method
     *            参数
     * @param classes
     *            参数
     * @return 返回值
     */
    private String getKey(Method method, Class[] classes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.getName());
        stringBuilder.append("(");
        for (Class clazz : classes) {
            stringBuilder.append(clazz.getSimpleName());
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.deleteCharAt(stringBuilder.toString().length() - 1);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private void enterMethod(Method method, Object... args) {
        Class<?> cls = method.getDeclaringClass();
        String methodName = method.getName();
        Object[] parameterValues = args;
        StringBuilder builder = new StringBuilder("\u21E2 ");
        builder.append(methodName).append('(');
        if (parameterValues != null) {
            for (int i = 0; i < parameterValues.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(BaseStrings.toString(parameterValues[i]));
            }
        }

        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }
        //Log.v(cls.getSimpleName(), builder.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            Trace.beginSection(section);
        }
    }

    private <T> BaseMethod loadDisplayBaseMethod(BaseProxy BaseProxy, Method method, Class<T> service) {
        synchronized (BaseProxy.methodCache) {
            String methodKey = getKey(method, method.getParameterTypes());
            BaseMethod baseMethod = BaseProxy.methodCache.get(methodKey);
            if (baseMethod == null) {
                baseMethod = baseMethod.createDisplayMethod(method, service);
                BaseProxy.methodCache.put(methodKey, baseMethod);
            }
            return baseMethod;
        }
    }

    public <T> T createImpl(final Class<T> service, final Object impl) {
        BaseCheckUtils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new BaseInvocationHandler() {

            @Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                // 业务拦截器 - 前
                for (ImplStartInterceptor item : BaseHelper.methodsProxy().implStartInterceptors) {
                    item.interceptStart(impl.getClass().getName(), service, method, args);
                }
                Object backgroundResult;
                if (!BaseHelper.isLogOpen()) {
                    backgroundResult = method.invoke(impl, args);// 执行
                } else {
                    enterMethod(method, args);
                    long startNanos = System.nanoTime();
                    backgroundResult = method.invoke(impl, args);// 执行
                    long stopNanos = System.nanoTime();
                    long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
                    exitMethod(method, backgroundResult, lengthMillis);
                }

                // 业务拦截器 - 后
                for (ImplEndInterceptor item : BaseHelper.methodsProxy().implEndInterceptors) {
                    item.interceptEnd(impl.getClass().getName(), service, method, args, backgroundResult);
                }
                return backgroundResult;
            }
        });
    }

    public BaseFragmentInterceptor getBaseFragmentInterceptor() {
        return skyFragmentInterceptor;
    }

    public BaseActivityInterceptor baseActivityInterceptor() {
        return baseActivityInterceptor;
    }

    public static class Builder {

        private BaseLayoutInterceptor baseLayoutInterceptor;		// 布局切换拦截器

        private BaseActivityInterceptor baseActivityInterceptor;		// activity拦截器

        private BaseFragmentInterceptor baseFragmentInterceptor;		// activity拦截器

        private ArrayList<BizStartInterceptor> baseStartInterceptors;		// 方法开始拦截器

        private ArrayList<BizEndInterceptor>		bizEndInterceptors;			// 方法结束拦截器

        private ArrayList<ImplStartInterceptor>		implStartInterceptors;		// 方法开始拦截器

        private ArrayList<ImplEndInterceptor>		implEndInterceptors;		// 方法结束拦截器

        private ArrayList<BaseBizErrorInterceptor> baseErrorInterceptors;		// 方法错误拦截器

        private DisplayStartInterceptor				displayStartInterceptor;	// 方法开始拦截器

        private DisplayEndInterceptor				displayEndInterceptor;		// 方法结束拦截器

        private ArrayList<BaseHttpErrorInterceptor> baseHttpErrorInterceptors;	// 网络错误拦截

        public void setActivityInterceptor(BaseActivityInterceptor baseActivityInterceptor) {
            this.baseActivityInterceptor = baseActivityInterceptor;
        }

        public void setFragmentInterceptor(BaseFragmentInterceptor baseFragmentInterceptor) {
            this.baseFragmentInterceptor = baseFragmentInterceptor;
        }

        public void setBaseLayoutInterceptor(BaseLayoutInterceptor baseLayoutInterceptor) {
            this.baseLayoutInterceptor = baseLayoutInterceptor;
        }

        public Builder addStartInterceptor(BizStartInterceptor bizStartInterceptor) {
            if (baseStartInterceptors == null) {
                baseStartInterceptors = new ArrayList<>();
            }
            if (!baseStartInterceptors.contains(bizStartInterceptor)) {
                baseStartInterceptors.add(bizStartInterceptor);
            }
            return this;
        }

        public Builder addEndInterceptor(BizEndInterceptor bizEndInterceptor) {
            if (bizEndInterceptors == null) {
                bizEndInterceptors = new ArrayList<>();
            }
            if (!bizEndInterceptors.contains(bizEndInterceptor)) {
                bizEndInterceptors.add(bizEndInterceptor);
            }
            return this;
        }

        public Builder setDisplayStartInterceptor(DisplayStartInterceptor displayStartInterceptor) {
            this.displayStartInterceptor = displayStartInterceptor;
            return this;
        }

        public Builder setDisplayEndInterceptor(DisplayEndInterceptor displayEndInterceptor) {
            this.displayEndInterceptor = displayEndInterceptor;
            return this;
        }

        public Builder addStartImplInterceptor(ImplStartInterceptor implStartInterceptor) {
            if (implStartInterceptors == null) {
                implStartInterceptors = new ArrayList<>();
            }
            if (!implStartInterceptors.contains(implStartInterceptor)) {
                implStartInterceptors.add(implStartInterceptor);
            }
            return this;
        }

        public Builder addEndImplInterceptor(ImplEndInterceptor implEndInterceptor) {
            if (implEndInterceptors == null) {
                implEndInterceptors = new ArrayList<>();
            }
            if (!implEndInterceptors.contains(implEndInterceptor)) {
                implEndInterceptors.add(implEndInterceptor);
            }
            return this;
        }

        public void addBizErrorInterceptor(BaseBizErrorInterceptor baseErrorInterceptor) {
            if (baseErrorInterceptors == null) {
                baseErrorInterceptors = new ArrayList<>();
            }
            if (!baseErrorInterceptors.contains(baseErrorInterceptor)) {
                baseErrorInterceptors.add(baseErrorInterceptor);
            }
        }

        public void addHttpErrorInterceptor(BaseHttpErrorInterceptor baseHttpErrorInterceptor) {
            if (baseHttpErrorInterceptors == null) {
                baseHttpErrorInterceptors = new ArrayList<>();
            }
            if (!baseHttpErrorInterceptors.contains(baseHttpErrorInterceptor)) {
                baseHttpErrorInterceptors.add(baseHttpErrorInterceptor);
            }
        }

        public BaseMethods build() {
            // 默认值
            ensureSaneDefaults();
            return new BaseMethods(baseLayoutInterceptor, baseActivityInterceptor, baseFragmentInterceptor, baseStartInterceptors, displayStartInterceptor, bizEndInterceptors, displayEndInterceptor,
                    implStartInterceptors, implEndInterceptors, baseErrorInterceptors, baseHttpErrorInterceptors);
        }

        private void ensureSaneDefaults() {
            if (baseStartInterceptors == null) {
                baseStartInterceptors = new ArrayList<>();
            }
            if (bizEndInterceptors == null) {
                bizEndInterceptors = new ArrayList<>();
            }
            if (baseErrorInterceptors == null) {
                baseErrorInterceptors = new ArrayList<>();
            }
            if (baseFragmentInterceptor == null) {
                baseFragmentInterceptor = BaseFragmentInterceptor.NONE;
            }
            if (baseActivityInterceptor == null) {
                baseActivityInterceptor = BaseActivityInterceptor.NONE;
            }
            if (baseLayoutInterceptor == null) {
                baseLayoutInterceptor = BaseLayoutInterceptor.NONE;
            }
            if (implStartInterceptors == null) {
                implStartInterceptors = new ArrayList<>();
            }
            if (implEndInterceptors == null) {
                implEndInterceptors = new ArrayList<>();
            }
            if (baseHttpErrorInterceptors == null) {
                baseHttpErrorInterceptors = new ArrayList<>();
            }
        }

    }

}








