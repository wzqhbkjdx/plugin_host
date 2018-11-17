package cent.news.com.baseframe.modules.structure;

import android.support.v4.app.FragmentManager;

import java.util.List;

import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.view.BaseActivity;

/**
 * Created by bym on 2018/6/20.
 */

public interface IBaseStructureManage {

    void attach(BaseStructureModel view);

    void detach(BaseStructureModel view);

    <B extends IBaseBiz> B biz(Class<B> bizClass);

    <B extends IBaseBiz> B biz(Class<B> bizClass, int position);

    <B extends IBaseBiz> boolean isExist(Class<B> bizClazz);

    <B extends IBaseBiz> boolean isExist(Class<B> biz, int position);

    <B extends IBaseBiz> List<B> bizList(Class<B> service);

    <T> T createMainLooper(final Class<T> service, Object ui);

    <T> T createMainLooperNotIntf(final Class<T> service, final Object ui);

    <U> U createNullService(final Class<U> service);

    boolean onKeyBack(int keyCode, FragmentManager fragmentManager, BaseActivity baseActivity);

    void printBackStackEntry(FragmentManager fragmentManager);

}
