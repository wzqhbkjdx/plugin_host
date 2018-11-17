package cent.news.com.baseframe.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.base.IBaseView;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.modules.structure.BaseStructureModel;
import cent.news.com.baseframe.utils.BaseAppUtil;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import cent.news.com.baseframe.utils.BaseKeyboardUtils;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseRVAdapter;

/**
 * Created by bym on 2018/6/19.
 */

public abstract class BaseDialogFragment<B extends IBaseBiz> extends DialogFragment implements IBaseDialogFragment,
        DialogInterface.OnKeyListener, IBaseView {

    private boolean targetActivity;

    protected int requestCode = 2013 << 5;

    public final static String ARG_REQUEST_CODE = "base_request_code";

    //View层编辑器
    public BaseBuilder baseBuilder;

    BaseStructureModel baseStructureModel;

    private Unbinder unbinder;

    protected abstract BaseBuilder build(BaseBuilder baseBuilder);

    protected void buildAfter(View view) {

    }

    protected void initDagger() {

    }

    protected void createData(Bundle savedInstanceState) {

    }

    protected abstract void initData(Bundle bundle);

    protected abstract int getDialogStyle();

    protected boolean isCancel() {
        return false;
    }

    protected void setDialogCancel(boolean fg) {
        getDialog().setCanceledOnTouchOutside(fg);
    }

    protected boolean isFull() {
        return false;
    }

    protected boolean isFullWidth() {
        return false;
    }

    protected boolean isFullHeight() {
        return false;
    }

    public boolean isTargetActivity() {
        return targetActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getDialogStyle());
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        final Fragment fragment = getTargetFragment();

        if(fragment != null) {
            requestCode = fragment.getTargetRequestCode();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        initCore();
        BaseHelper.structureHelper().attach(baseStructureModel);
        baseBuilder = new BaseBuilder(this, inflater);
        View view = build(baseBuilder).create();
        unbinder = ButterKnife.bind(this, view);
        setDialogCancel(isCancel());
        getDialog().setOnKeyListener(this);
        buildAfter(view);
        return view;
    }

    private void initCore() {
        baseStructureModel = new BaseStructureModel(this, getArguments());
    }

    public Object model() {
        return baseStructureModel.getBaseProxy().impl;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isFull()) {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else if(isFullWidth()) {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if(isFullHeight()) {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        baseStructureModel.initBizBundle();

        initDagger();

        createData(savedInstanceState);

        initData(getArguments());
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseHelper.structureHelper().printBackStackEntry(getFragmentManager());
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(baseBuilder != null) {
            baseBuilder.detach();
            baseBuilder = null;
        }

        if(baseStructureModel != null) {
            BaseHelper.structureHelper().detach(baseStructureModel);
        }

        unbinder.unbind();

        BaseKeyboardUtils.hideSoftInput(getActivity());

        if(getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
    }

    protected void detach() {

    }

    public void setSoftInput(int mode) {
        getActivity().getWindow().setSoftInputMode(mode);
    }

    public B biz() {
        if (baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
            Class service = BaseAppUtil.getSuperClassGenricType(getClass(), 0);
            return (B) BaseHelper.structureHelper().createNullService(service);
        }
        return (B) baseStructureModel.getBaseProxy().proxy;
    }

    public <C extends IBaseBiz> C biz(Class<C> service) {
        if (baseStructureModel != null && service.equals(baseStructureModel.getService())) {
            if (baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
                return BaseHelper.structureHelper().createNullService(service);
            }
            return (C) baseStructureModel.getBaseProxy().proxy;
        }
        return BaseHelper.biz(service);
    }

    public <D extends BaseIDisplay> D display(Class<D> eClass) {
        return BaseHelper.display(eClass);
    }


    public boolean onKeyBack() {
        dismissAllowingStateLoss();
        return true;
    }

    public void recyclerRefreshing(boolean bool) {
        if(baseBuilder != null) {
            baseBuilder.recyclerRefreshing(bool);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (baseBuilder.getToolbarMenuId() > 0) {
            menu.clear();
            this.getActivity().getMenuInflater().inflate(baseBuilder.getToolbarMenuId(), menu);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseHelper.methodsProxy().baseActivityInterceptor().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public <T> T findFragment(Class<T> clazz) {
        BaseCheckUtils.checkNotNull(clazz, "class不能为空");
        return (T) getFragmentManager().findFragmentByTag(clazz.getName());
    }


    /********************ActionBar业务代码*********************/
    @Override
    public void showContent() {
        if(baseBuilder != null) {
            baseBuilder.layoutContent();
        }
    }

    @Override
    public void showLoading() {
        if(baseBuilder != null) {
            baseBuilder.layoutLoading();
        }
    }

    @Override
    public void showBizError() {
        if(baseBuilder != null) {
            baseBuilder.layoutBizError();
        }
    }

    @Override
    public void showEmpty() {
        if(baseBuilder != null) {
            baseBuilder.layoutEmpty();
        }
    }

    @Override
    public void showHttpError() {
        if(baseBuilder != null) {
            baseBuilder.layoutHttpError();
            recyclerRefreshing(false);
        }
    }

    @Override
    public int showState() {
        if(baseBuilder != null) {
            return baseBuilder.getLayoutState();
        } else {
            return IBaseView.STATE_CONTENT;
        }
    }

    @Override
    public <T extends BaseRVAdapter> T adapter() {
        return baseBuilder == null ? null : (T) baseBuilder.getBaseRVAdapterItem();
    }

    public Toolbar toolbar() {
        return baseBuilder == null ? null : baseBuilder.getToolbar();
    }

    /*******************recyclerView 业务代码***********************/

    public RecyclerView.LayoutManager layoutManager() {
        return baseBuilder == null ? null : baseBuilder.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return baseBuilder == null ? null : baseBuilder.getRecyclerView();
    }

    public SwipeRefreshLayout swipeRefreshLayout() {
        if(baseBuilder == null) {
            return null;
        }
        return baseBuilder.getSwipeContainer();
    }

    public BaseView baseView() {
        return baseBuilder == null ? null : baseBuilder.getBaseView();
    }

    protected void onVisible() {

    }

    protected void onInvisible() {

    }

    /********************Dialog业务代码*********************/

    protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        List<T> listeners = new ArrayList<>(2);
        if(targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            listeners.add((T) targetFragment);
        }
        if(getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            listeners.add((T) getActivity());
        }
        return Collections.unmodifiableList(listeners);
    }


    private List<IDialogCancelListener> getListeners() {
        return getDialogListeners(IDialogCancelListener.class);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        for(IDialogCancelListener listener : getListeners()) {
            listener.onCancelled(requestCode);
        }
    }

    @Override public DialogFragment show(FragmentManager fragmentManager) {
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment show(FragmentManager fragmentManager, int mRequestCode) {
        this.requestCode = mRequestCode;
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mTargetFragment
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment) {
        this.setTargetFragment(mTargetFragment, requestCode);
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mTargetFragment
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode) {
        this.setTargetFragment(mTargetFragment, mRequestCode);
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param activity
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment show(FragmentManager fragmentManager, Activity activity) {
        this.targetActivity = true;
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param activity
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment show(FragmentManager fragmentManager, Activity activity, int mRequestCode) {
        this.targetActivity = true;
        this.requestCode = mRequestCode;
        show(fragmentManager, this.getClass().getName());
        return this;
    }

    /**
     * @param item
     *            参数
     * @return 返回值
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示碎片-不保存activity状态
     *
     * @return 返回值 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, int mRequestCode) {
        this.requestCode = mRequestCode;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mTargetFragment
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment) {
        if (mTargetFragment != null) {
            this.setTargetFragment(mTargetFragment, requestCode);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param mTargetFragment
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode) {
        if (mTargetFragment != null) {
            this.setTargetFragment(mTargetFragment, mRequestCode);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param activity
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity) {
        this.targetActivity = true;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param fragmentManager
     *            参数
     * @param activity
     *            参数
     * @param mRequestCode
     *            参数
     * @return 返回值
     */
    @Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity, int mRequestCode) {
        this.targetActivity = true;
        this.requestCode = mRequestCode;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * @param dialog
     *            参数
     * @param keyCode
     *            参数
     * @param event
     *            参数
     * @return 返回值
     */
    @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onKeyBack();
        } else {
            return false;
        }
    }

    /**
     * 获取内容视图
     *
     * @return 视图
     */
    protected View contentView() {
        return baseBuilder.getContentRootView();
    }
}









