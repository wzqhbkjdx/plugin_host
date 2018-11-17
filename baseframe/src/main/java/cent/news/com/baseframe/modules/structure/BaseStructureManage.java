package cent.news.com.baseframe.modules.structure;

import android.support.v4.app.FragmentManager;
import android.support.v4.util.SimpleArrayMap;
import android.view.KeyEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.R;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.modules.log.L;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import cent.news.com.baseframe.view.BaseActivity;
import cent.news.com.baseframe.view.BaseFragment;
import sky.cglib.proxy.Enhancer;
import sky.cglib.proxy.MethodInterceptor;

/**
 * Created by bym on 2018/6/20.
 */

public class BaseStructureManage implements IBaseStructureManage {

    private final ConcurrentHashMap<Class<?>, SimpleArrayMap<Integer, BaseStructureModel>> stackRepeatBiz;

    public BaseStructureManage() {
        stackRepeatBiz = new ConcurrentHashMap<>();
    }

    @Override
    public void attach(BaseStructureModel view) {
        synchronized (stackRepeatBiz) {
            if (view.getService() == null) {
                return;
            }
            SimpleArrayMap<Integer, BaseStructureModel> stack = stackRepeatBiz.get(view.getService());
            if (stack == null) {
                stack = new SimpleArrayMap();
            }
            stack.put(view.key, view);

            stackRepeatBiz.put(view.getService(), stack);

            if (BaseHelper.isLogOpen()) {
                L.tag("SKYStructureManage");
                L.i(view.getView().getClass().getSimpleName() + " -- stack:put(" + view.key + ")");
            }
        }
    }

    @Override
    public void detach(BaseStructureModel view) {
        synchronized (stackRepeatBiz) {
            if(view.getService() == null) {
                return;
            }

            SimpleArrayMap<Integer, BaseStructureModel> stack = stackRepeatBiz.get(view.getService());

            if (stack == null) {
                return;
            }

            BaseStructureModel BaseStructureModel = stack.get(view.key);

            stack.remove(view.key);

            if (stack.size() < 1) {
                stackRepeatBiz.remove(view.getService());
            }

            if (BaseHelper.isLogOpen()) {
                //L.tag("SKYStructureManage");
                //L.i(view.getView().getClass().getSimpleName() + " -- stack:remove(" + view.key + ")");

                //L.tag("SKYStructureManage");
                StringBuilder builder = new StringBuilder("\u21E0 ");
                builder.append("SKYStructureManage.stackRepeatBiz").append('(');
                if (stackRepeatBiz != null && stackRepeatBiz.size() > 0) {
                    for (Class clazz : stackRepeatBiz.keySet()) {
                        builder.append(clazz.getSimpleName());
                        builder.append(", ");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                }

                builder.append(')');
            }

            if (BaseStructureModel != null) {
                BaseStructureModel.clearAll();
            }


        }
    }

    @Override
    public <B extends IBaseBiz> B biz(Class<B> bizClass) {
        return biz(bizClass, 0);
    }

    @Override
    public <B extends IBaseBiz> B biz(Class<B> bizClass, int position) {
        SimpleArrayMap<Integer, BaseStructureModel> stack = stackRepeatBiz.get(bizClass);
        if (stack == null) {
            Set<Map.Entry<Class<?>, SimpleArrayMap<Integer, BaseStructureModel>>> entrySet = stackRepeatBiz.entrySet();
            for (Map.Entry<Class<?>, SimpleArrayMap<Integer, BaseStructureModel>> entry : entrySet) {
                SimpleArrayMap<Integer, BaseStructureModel> simpleArrayMap = entry.getValue();
                if (simpleArrayMap.valueAt(position).isSuperClass(bizClass)) {
                    return (B) simpleArrayMap.valueAt(position).getBaseProxy().proxy;
                }
            }

            return createNullService(bizClass);
        }
        BaseStructureModel SKYStructureModel = stack.valueAt(position);
        if (SKYStructureModel == null) {
            return createNullService(bizClass);
        }
        if (SKYStructureModel.getBaseProxy() == null || SKYStructureModel.getBaseProxy().proxy == null) {
            return createNullService(bizClass);
        }
        return (B) SKYStructureModel.getBaseProxy().proxy;
    }

    @Override
    public <B extends IBaseBiz> boolean isExist(Class<B> bizClazz) {
        return isExist(bizClazz, 0);
    }

    @Override
    public <B extends IBaseBiz> boolean isExist(Class<B> biz, int position) {
        SimpleArrayMap<Integer, BaseStructureModel> stack = stackRepeatBiz.get(biz);
        if (stack == null) {
            Set<Map.Entry<Class<?>, SimpleArrayMap<Integer, BaseStructureModel>>> entrySet = stackRepeatBiz.entrySet();
            for (Map.Entry<Class<?>, SimpleArrayMap<Integer, BaseStructureModel>> entry : entrySet) {
                SimpleArrayMap<Integer, BaseStructureModel> simpleArrayMap = entry.getValue();
                if (simpleArrayMap.valueAt(position).isSuperClass(biz)) {
                    return true;
                }
            }
            return false;
        }
        BaseStructureModel baseStructureModel = stack.valueAt(0);
        if (baseStructureModel == null) {
            return false;
        }
        if (baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
            return false;
        }
        return true;
    }

    @Override
    public <B extends IBaseBiz> List<B> bizList(Class<B> service) {
        SimpleArrayMap<Integer, BaseStructureModel> stack = stackRepeatBiz.get(service);
        List list = new ArrayList();
        if (stack == null) {
            return list;
        }
        int count = stack.size();
        for (int i = 0; i < count; i++) {
            BaseStructureModel baseStructureModel = stack.valueAt(i);
            if (baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
                list.add(createNullService(service));
            } else {
                list.add(baseStructureModel.getBaseProxy().proxy);
            }
        }
        return list;
    }

    @Override
    public <T> T createMainLooper(Class<T> service, final Object ui) {
        BaseCheckUtils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {

            @Override public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                // 如果有返回值 - 直接执行
                if (!method.getReturnType().equals(void.class)) {
                    if (ui == null) {
                        return null;
                    }
                    return method.invoke(ui, args);
                }

                // 如果是主线程 - 直接执行
                if (!BaseHelper.isMainLooperThread()) {// 子线程
                    if (ui == null) {
                        return null;
                    }
                    return method.invoke(ui, args);
                }
                Runnable runnable = new Runnable() {

                    @Override public void run() {
                        try {
                            if (ui == null) {
                                return;
                            }
                            method.invoke(ui, args);
                        } catch (Exception throwable) {
                            if (BaseHelper.isLogOpen()) {
                                throwable.printStackTrace();
                            }
                            return;
                        }
                    }
                };
                BaseHelper.mainLooper().execute(runnable);
                return null;
            }
        });
    }

    @Override public <T> T createMainLooperNotIntf(final Class<T> service, final Object ui) {
        Enhancer e = new Enhancer(BaseHelper.getInstance());
        e.setSuperclass(service);
        e.setInterceptor(new MethodInterceptor() {

            @Override public Object intercept(String name, Class[] argsType, final Object[] args) throws Exception {
                final Method method = service.getMethod(name, argsType);

                // 如果有返回值 - 直接执行
                if (!method.getReturnType().equals(void.class)) {
                    if (ui == null) {
                        return null;
                    }
                    return method.invoke(ui, args);
                }

                // 如果是主线程 - 直接执行
                if (!BaseHelper.isMainLooperThread()) {// 子线程
                    if (ui == null) {
                        return null;
                    }
                    return method.invoke(ui, args);
                }
                Runnable runnable = new Runnable() {

                    @Override public void run() {
                        try {
                            if (ui == null) {
                                return;
                            }
                            method.invoke(ui, args);
                        } catch (Exception throwable) {
                            if (BaseHelper.isLogOpen()) {
                                throwable.printStackTrace();
                            }
                            return;
                        }
                    }
                };
                BaseHelper.mainLooper().execute(runnable);
                return null;
            }
        });
        return (T) e.create();
    }

    @Override
    public <U> U createNullService(final Class<U> service) {
        if (!service.isInterface()) {
            return createNullServiceNotInf(service);
        }

        return (U) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {

            @Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {

                if (BaseHelper.isLogOpen()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("UI被销毁,回调接口继续执行");
                    stringBuilder.append("方法[");
                    stringBuilder.append(method.getName());
                    stringBuilder.append("]");
                    //L.tag(service.getSimpleName());
                    //L.i(stringBuilder.toString());
                }

                if (method.getReturnType().equals(int.class) || method.getReturnType().equals(long.class) || method.getReturnType().equals(float.class) || method.getReturnType().equals(double.class)
                        || method.getReturnType().equals(short.class)) {
                    return 0;
                }

                if (method.getReturnType().equals(boolean.class)) {
                    return false;
                }
                if (method.getReturnType().equals(byte.class)) {
                    return Byte.parseByte(null);
                }
                if (method.getReturnType().equals(char.class)) {
                    return ' ';
                }
                return null;
            }
        });
    }


    public <U> U createNullServiceNotInf(final Class<U> service) {
        Enhancer e = new Enhancer(BaseHelper.getInstance());
        e.setSuperclass(service);
        e.setInterceptor(new MethodInterceptor() {

            @Override public Object intercept(String name, Class[] argsType, final Object[] args) throws Exception {
                final Method method = service.getMethod(name, argsType);
                if (BaseHelper.isLogOpen()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("UI被销毁,回调接口继续执行");
                    stringBuilder.append("方法[");
                    stringBuilder.append(method.getName());
                    stringBuilder.append("]");
                    L.tag(service.getSimpleName());
                    L.i(stringBuilder.toString());
                }

                if (method.getReturnType().equals(int.class) || method.getReturnType().equals(long.class) || method.getReturnType().equals(float.class) || method.getReturnType().equals(double.class)
                        || method.getReturnType().equals(short.class)) {
                    return 0;
                }

                if (method.getReturnType().equals(boolean.class)) {
                    return false;
                }
                if (method.getReturnType().equals(byte.class)) {
                    return Byte.parseByte(null);
                }
                if (method.getReturnType().equals(char.class)) {
                    return ' ';
                }
                return null;
            }
        });
        return (U) e.create();
    }


    @Override
    public boolean onKeyBack(int keyCode, FragmentManager fragmentManager, BaseActivity baseActivity) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            int idx = fragmentManager.getBackStackEntryCount();
            if (idx > 1) {
                FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(idx - 1);
                Object view = fragmentManager.findFragmentByTag(entry.getName());
                if (view instanceof BaseFragment) {
                    return ((BaseFragment) view).onKeyBack();
                }
            } else {
                Object view = fragmentManager.findFragmentById(R.id.sky_home);
                if (view instanceof BaseFragment) {
                    return ((BaseFragment) view).onKeyBack();
                }
            }
            if (baseActivity != null) {
                return baseActivity.onKeyBack();
            }
        }
        return false;
    }

    @Override
    public void printBackStackEntry(FragmentManager fragmentManager) {
        if (BaseHelper.isLogOpen()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");

            int count = fragmentManager.getBackStackEntryCount();

            for (int i = 0; i < count; i++) {
                stringBuilder.append(",");
                stringBuilder.append(fragmentManager.getBackStackEntryAt(i).getName());
            }
            stringBuilder.append("]");
            stringBuilder.deleteCharAt(1);
            //L.tag("Activity FragmentManager:");
            //L.i(stringBuilder.toString());
        }
    }
}
