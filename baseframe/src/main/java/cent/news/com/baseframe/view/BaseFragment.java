package cent.news.com.baseframe.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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

public abstract class BaseFragment<B extends IBaseBiz> extends Fragment implements View.OnTouchListener, IBaseView {

    private boolean targetActivity;

    BaseStructureModel baseStructureModel;

    private Unbinder unbinder;

    private BaseBuilder baseBuilder;

    protected abstract BaseBuilder build(BaseBuilder builder);

    protected abstract void buildAfter(View view);

    protected void initDagger() {

    }


    protected void createData(Bundle savedInstanceState) {

    }

    protected void initData(Bundle savedInstanceState) {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initCore();
        BaseHelper.structureHelper().attach(baseStructureModel);
        baseBuilder = new BaseBuilder(this, inflater);
        View view = build(baseBuilder).create();

        unbinder = ButterKnife.bind(this, view);

        view.setOnTouchListener(this);

        BaseHelper.methodsProxy().getBaseFragmentInterceptor().buildAfter(this);

        buildAfter(view);

        return view;
    }

    private void initCore() {
        baseStructureModel = new BaseStructureModel(this, getArguments());
    }

    public Object model() {
        return baseStructureModel.getBaseProxy().impl;
    }

    public boolean isTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(boolean targetActivity) {
        this.targetActivity = targetActivity;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentCreated(this, getArguments(), savedInstanceState);
        baseStructureModel.initBizBundle();
        initDagger();
        createData(savedInstanceState);
        initData(getArguments());
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentResume(this);
        BaseHelper.structureHelper().printBackStackEntry(getFragmentManager());
    }

    @Override
    public void onPause() {
        super.onPause();
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentPause(this);
        recyclerRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseHelper.methodsProxy().baseActivityInterceptor().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentStop(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recyclerRefreshing(boolean bool) {
        if(baseBuilder != null) {
            baseBuilder.recyclerRefreshing(bool);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        detach();

        if(baseBuilder != null) {
            baseBuilder.detach();
            baseBuilder = null;
        }

        if(baseStructureModel != null) {
            BaseHelper.structureHelper().detach(baseStructureModel);
        }

        //清空注解
        unbinder.unbind();

        BaseKeyboardUtils.hideSoftInput(getActivity());
    }


    public <D extends BaseIDisplay> D display(Class<D> clazz) {
        return BaseHelper.display(clazz);
    }


    protected void detach() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseHelper.methodsProxy().getBaseFragmentInterceptor().onFragmentDestroy(this);
    }

    public B biz() {
        if(baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
            //获取父类中的范型类，也就是Biz类
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


    /**
     * 拦截事件
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public void setSoftInputMode(int mode) {
        getActivity().getWindow().setSoftInputMode(mode);
    }

    public <T> T findFragment(Class<T> clazz) {
        BaseCheckUtils.checkNotNull(clazz, "class不能为空");
        return (T) getFragmentManager().findFragmentByTag(clazz.getName());
    }

    protected <A extends BaseActivity> A activity() {
        return (A) getActivity();
    }

    public BaseView baseView() {
        return baseBuilder == null ? null : baseBuilder.getBaseView();
    }

    @Override
    public void showContent() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showContent(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutContent();
        }
    }

    @Override
    public void showEmpty() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showEmpty(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutEmpty();
        }
    }

    @Override
    public void showBizError() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showBizError(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutBizError();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showHttpError() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showHttpError(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutHttpError();
        }
    }

    @Override
    public int showState() {
        if(baseBuilder != null) {
            baseBuilder.getLayoutState();
        }
        return IBaseView.STATE_CONTENT;
    }

    @Override
    public <T extends BaseRVAdapter> T adapter() {
        return baseBuilder == null ? null : (T) baseBuilder.getBaseRVAdapterItem();
    }

    public Toolbar toolbar() {
        return baseBuilder == null ? null : baseBuilder.getToolbar();
    }

    public boolean onKeyBack() {
        getFragmentManager().popBackStack();
        return true;
    }

    public RecyclerView.LayoutManager layoutManager() {
        return baseBuilder == null ? null : baseBuilder.getLayoutManager();
    }

    public RecyclerView recyclerView() {
        return baseBuilder == null ? null : baseBuilder.getRecyclerView();
    }

    public SwipeRefreshLayout swipeRefreshLayout() {
        if(baseBuilder != null) {
            return baseBuilder.getSwipeContainer();
        }
        return null;
    }

    /****************** ViewPager 业务代码******************/
    public void onVisible() {}

    public void inVisible() {}

    protected View contentView() {
        return baseBuilder.getContentRootView();
    }

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }
}












