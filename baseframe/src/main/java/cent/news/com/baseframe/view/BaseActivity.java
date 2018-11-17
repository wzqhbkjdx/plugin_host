package cent.news.com.baseframe.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.base.IBaseView;
import cent.news.com.baseframe.common.BaseSwipeWindowHelper;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.modules.structure.BaseStructureModel;
import cent.news.com.baseframe.utils.BaseAppUtil;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import cent.news.com.baseframe.utils.BaseKeyboardUtils;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseRVAdapter;

/**
 * Created by bym on 2018/6/16.
 */

public abstract class BaseActivity<B extends IBaseBiz> extends AppCompatActivity implements IBaseView {

    protected abstract BaseBuilder build(BaseBuilder builder);

    protected void buildBefore(Bundle bundle) {

    }

    protected void initDagger() {

    }

    protected void createData(Bundle savedInstanceState) {

    }

    protected abstract void initData(Bundle savedInstanceState);

    private BaseBuilder baseBuilder;

    BaseStructureModel baseStructureModel;

    private SystemBarTintManager systemBarTintManager;

    private BaseSwipeWindowHelper baseSwipeWindowHelper;

    private boolean isFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        buildBefore(savedInstanceState);
        super.onCreate(savedInstanceState);

        //初始化核心
        initCore();

        //初始化screen堆栈
        BaseHelper.screenHelper().onCreate(this);

        //Activity拦截器，如果BaseStructureManage中的BaseMethods被初始化了，则
        //BaseActivityInterceptor的onCreate()函数可以在所有继承了BaseActivity的子Activity中执行。
        BaseHelper.methodsProxy().baseActivityInterceptor().onCreate(this,
                getIntent().getExtras(), savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseBuilder = new BaseBuilder(this, inflater);

        //Builder拦截
        BaseHelper.methodsProxy().baseActivityInterceptor().build(this, baseBuilder);
        setContentView(build(baseBuilder).create());

        //状态栏高度
        if(baseBuilder.isFitsSystem()) {
            ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(true);
            }
        }

        //状态栏颜色
        if(baseBuilder.isTint()) {
            systemBarTintManager = new SystemBarTintManager(this);
            // enable status bar tint
            systemBarTintManager.setStatusBarTintEnabled(baseBuilder.getStatusBarTintEnabled());
            // enable navigation bar tint
            systemBarTintManager.setNavigationBarTintEnabled(baseBuilder.getNavigationBarTintEnabled());
            systemBarTintManager.setStatusBarTintResource(baseBuilder.getTintColor());
        }

        //初始化所有组件
        ButterKnife.bind(this);

        //初始化业务数据
        if(baseStructureModel != null) {
            baseStructureModel.initBizBundle();
        }

        initDagger();

        createData(savedInstanceState);

        initData(getIntent().getExtras());
    }

    public Object model() {
        return baseStructureModel.getBaseProxy().impl;
    }


    private void initCore() {
        baseStructureModel = new BaseStructureModel(this, getIntent() == null ? null : getIntent().getExtras());
        BaseHelper.structureHelper().attach(baseStructureModel);
    }

    public Toolbar toolbar() {
        return baseBuilder == null ? null : baseBuilder.getToolbar();
    }

    public B biz() {
        if(baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
            Class service = BaseAppUtil.getSuperClassGenricType(getClass(), 0);
            return (B) BaseHelper.structureHelper().createNullService(service);
        }
        return (B) baseStructureModel.getBaseProxy().proxy;
    }

    public <C extends IBaseBiz> C biz(Class<C> service) {
        if(baseStructureModel != null && service.equals(baseStructureModel.getService())) {
            if(baseStructureModel == null || baseStructureModel.getBaseProxy() == null || baseStructureModel.getBaseProxy().proxy == null) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
        return true;
    }

    public boolean canBeSlideBack() {
        return true;
    }

    public void setSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
    }

    public void recyclerRefreshing(boolean bool) {
        if(baseBuilder != null) {
            baseBuilder.recyclerRefreshing(bool);
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        BaseHelper.methodsProxy().baseActivityInterceptor().onPostCreate(this, savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BaseHelper.methodsProxy().baseActivityInterceptor().onPostResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseHelper.methodsProxy().baseActivityInterceptor().onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseHelper.screenHelper().onResume(this);
        BaseHelper.methodsProxy().baseActivityInterceptor().onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BaseHelper.screenHelper().onActivityResult(this);
        BaseHelper.methodsProxy().baseActivityInterceptor().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseHelper.screenHelper().onPause(this);
        BaseHelper.methodsProxy().baseActivityInterceptor().onPause(this);

        //恢复初始化
        recyclerRefreshing(false);

        if(isFinishing()) {
            isFinish = true;

            detach();

            //移除builder
            if(baseBuilder != null) {
                baseBuilder.detach();
                baseBuilder = null;
            }

            if(baseStructureModel != null) {
                BaseHelper.structureHelper().detach(baseStructureModel);
            }

            BaseHelper.screenHelper().onDestroy(this);
            BaseHelper.methodsProxy().baseActivityInterceptor().onDestroy(this);
            BaseKeyboardUtils.hideSoftInput(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(finishIn != -999 && finishOut != -999) {
            overridePendingTransition(finishIn, finishOut);
        }
    }

    private int finishIn = -999;
    private int finishOut = -999;

    protected void setFinishAnim(int finishIn, int finishOut) {
        this.finishIn = finishIn;
        this.finishOut = finishOut;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isFinish) {
            detach();

            //移除builder
            if(baseBuilder != null) {
                baseBuilder.detach();
                baseBuilder = null;
            }

            if(baseStructureModel != null) {
                BaseHelper.structureHelper().detach(baseStructureModel);
            }

            BaseHelper.screenHelper().onDestroy(this);
            BaseHelper.methodsProxy().baseActivityInterceptor().onDestroy(this);
            BaseKeyboardUtils.hideSoftInput(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseHelper.methodsProxy().baseActivityInterceptor().onRestart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseHelper.methodsProxy().baseActivityInterceptor().onStop(this);
    }

    protected void detach() {

    }

    public void setLanding() {
        BaseHelper.screenHelper().setAsLanding(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(baseBuilder != null && baseBuilder.getToolbarMenuId() > 0) {
            getMenuInflater().inflate(baseBuilder.getToolbarMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(BaseHelper.structureHelper().onKeyBack(keyCode, getSupportFragmentManager(), this)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * View业务代码
     */
    public <T> T findFragment(Class<T> clazz) {
        BaseCheckUtils.checkNotNull(clazz, "class不能为空");
        return (T) getSupportFragmentManager().findFragmentByTag(clazz.getName());
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
    public void showLoading() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showLoading(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutLoading();
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
    public void showEmpty() {
        if(BaseHelper.methodsProxy().interceptor() != null) {
            BaseHelper.methodsProxy().interceptor().showEmpty(this);
        }
        if(baseBuilder != null) {
            baseBuilder.layoutEmpty();
        }
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
            return baseBuilder.getLayoutState();
        } else {
            return IBaseView.STATE_CONTENT;
        }
    }

    @Override
    public <T extends BaseRVAdapter> T adapter() {
        return baseBuilder == null ? null : (T) baseBuilder.getBaseRVAdapterItem();
    }

    public RecyclerView.LayoutManager layoutManager() {
        return baseBuilder == null ? null : baseBuilder.getLayoutManager();
    }

    public <R extends RecyclerView> R recyclerView() {
        return baseBuilder == null ? null : (R) baseBuilder.getRecyclerView();
    }

    public SwipeRefreshLayout swipeRefresh() {
        if(baseBuilder == null) {
            return null;
        }
        return baseBuilder.getSwipeContainer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseHelper.methodsProxy().baseActivityInterceptor().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!supportSlideBack()) {
            return super.dispatchTouchEvent(ev);
        }

        if(baseSwipeWindowHelper == null) {
            baseSwipeWindowHelper = new BaseSwipeWindowHelper(getWindow());
        }

        return baseSwipeWindowHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);

    }

    protected boolean supportSlideBack() {
        if(baseBuilder == null) {
            return false;
        }
        return baseBuilder.isOpenSwipBackLayout();
    }

    protected void swipeWindowEdge(int edge) {
        if(baseSwipeWindowHelper == null) {
            return;
        }

        baseSwipeWindowHelper.setEdgeSize(edge);
    }

    protected int swipeEdgeDefaultSize() {
        if(baseSwipeWindowHelper == null) {
            return 0;
        }

        return baseSwipeWindowHelper.getEdgeDefalutSize();
    }

    /**
     * 能否滑动返回至当前activity
     */
    public boolean canSlideBack() {
        return true;
    }

    protected View contentView() {
        return baseBuilder.getContentRootView();
    }

    protected SystemBarTintManager tintManager() {
        return systemBarTintManager;
    }
}











