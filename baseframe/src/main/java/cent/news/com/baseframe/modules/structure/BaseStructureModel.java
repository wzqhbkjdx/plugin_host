package cent.news.com.baseframe.modules.structure;

import android.os.Bundle;

import java.util.Stack;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.BaseBiz;
import cent.news.com.baseframe.modules.methodsProxy.BaseProxy;
import cent.news.com.baseframe.utils.BaseAppUtil;

/**
 * Created by bym on 2018/6/19.
 *
 * View和Biz的核心类，连接View和Biz，通过cglib对View和Biz在字节码层面进行修改和代理。
 *
 */

public class BaseStructureModel {


    final int				key;

    BaseProxy               baseProxy;

    private Object			view;

    private Bundle bundle;

    private Class			service;

    private Stack<Class> supper;

    private Object			impl;

    private Object			biz;

    public BaseStructureModel(Object view, Bundle bundle) {
        //唯一标记
        key = view.hashCode();

        //View
        this.view = view;

        //数据
        this.bundle = bundle;

        //业务类初始化
        service = BaseAppUtil.getClassGenricType(view.getClass(), 0);

        if(service == null) {
            return;
        }

        if(!service.isInterface()) {
            impl = BaseAppUtil.getImplClassNotInf(service);

            //找非BaseBiz的父类
            supper = new Stack<>();
            Class tempClass = impl.getClass().getSuperclass();

            if(tempClass != null) {
                while(!tempClass.equals(BaseBiz.class)) {
                    if(tempClass.getSuperclass() != null) {
                        Class clazz = tempClass.getSuperclass();
                        supper.add(clazz);
                    }
                    tempClass = tempClass.getSuperclass();
                }
            }

            //如果是业务类
            if(impl instanceof BaseBiz) {
                ((BaseBiz) impl).initUI(this);
            }

            baseProxy = BaseHelper.methodsProxy().createNotInf(service, impl);

        } else {
            impl = BaseAppUtil.getImplClass(service);

            supper = new Stack<>();
            Class tempClass = impl.getClass().getSuperclass();

            if(tempClass != null) {
                while (!tempClass.equals(BaseBiz.class)) {

                    if(tempClass.getInterfaces() != null) {
                        Class clazz = tempClass.getInterfaces()[0];
                        supper.add(clazz);
                    }

                    tempClass = tempClass.getSuperclass();

                }
            }

            //如果是业务类
            if(impl instanceof BaseBiz) {
                ((BaseBiz) impl).initUI(this);
            }

            baseProxy = BaseHelper.methodsProxy().create(service, impl);
        }

    }

    public void initBizBundle() {
        if (impl instanceof BaseBiz) {
            ((BaseBiz) impl).initBundle();
        }
    }

    /**
     * 清空
     */
    public void clearAll() {
        this.bundle = null;
        this.view = null;
        service = null;
        if (this.impl != null) {
            ((BaseBiz) impl).detach();
            impl = null;
        }
        if (this.biz != null) {
            ((BaseBiz) biz).detach();
            biz = null;
        }
        if (baseProxy != null) {
            baseProxy.clearProxy();
            baseProxy = null;
        }
        if (supper != null) {
            supper.clear();
            supper = null;
        }
    }

    public int getKey() {
        return key;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Object getView() {
        return view;
    }

    public Class getService() {
        return service;
    }

    public BaseProxy getBaseProxy() {
        return baseProxy;
    }

    public boolean isSuperClass(Class clazz) {
        if (supper == null || clazz == null) {
            return false;
        }
        return supper.search(clazz) != -1;
    }



}

















