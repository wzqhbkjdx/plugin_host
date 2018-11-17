package cent.news.com.baseframe.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.R;
import cent.news.com.baseframe.base.IBaseView;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import cent.news.com.baseframe.utils.BaseKeyboardUtils;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseFooterOnScrollListener;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseOnScrollListener;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseRVAdapter;
import cent.news.com.baseframe.view.adapter.recyclerView.BaseStickyHeaders;
import cent.news.com.baseframe.view.adapter.recyclerView.StickyRecyclerHeadersDecoration;
import cent.news.com.baseframe.view.adapter.recyclerView.StickyRecyclerHeadersTouchListener;
import cent.news.com.baseframe.view.common.BaseFooterListener;
import cent.news.com.baseframe.view.common.BaseRefreshListener;

/**
 * Created by bym on 2018/6/19.
 */

public final class BaseBuilder {

    private BaseView baseView;

    private LayoutInflater mInflater;

    private Toolbar toolbar;

    public BaseBuilder(@NonNull BaseActivity baseActivity, @NonNull LayoutInflater inflater) {
        baseView = new BaseView();
        baseView.initUI(baseActivity);
        this.mInflater = inflater;
    }


    public BaseBuilder(@NonNull BaseFragment baseFragment, @NonNull LayoutInflater inflater) {
        baseView = new BaseView();
        baseView.initUI(baseFragment);
        this.mInflater = inflater;
    }

    public BaseBuilder(@NonNull BaseDialogFragment baseDialogFragment, @NonNull LayoutInflater inflater) {
        baseView = new BaseView();
        baseView.initUI(baseDialogFragment);
        this.mInflater = inflater;
    }


    @Nullable Toolbar getToolbar() {
        return toolbar;
    }

    @Nullable BaseView getBaseView() {
        return baseView;
    }

    private int layoutId;

    private int layoutStateId;

    private FrameLayout contentRoot;

    private View contentRootView;

    int getLayoutId() {
        return layoutId;
    }

    public void layoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public void layoutStateId(@IdRes int layoutStateId) {
        this.layoutStateId = layoutStateId;
    }

    /**
     * 显示状态切换
     */
    int showState;

    private int layoutLoadingId;

    private int layoutEmptyId;

    private int layoutBizErrorId;

    private int layoutHttpErrorId;

    private int layoutBackground;

    private View layoutContent;

    private View layoutStateContent;

    private ViewStub vsLoading;

    private View layoutLoading;

    private View layoutEmpty;

    private View layoutBizError;

    private View layoutHttpError;

    // 设置
    public void layoutLoadingId(@LayoutRes int layoutLoadingId) {
        this.layoutLoadingId = layoutLoadingId;
    }

    public void layoutEmptyId(@LayoutRes int layoutEmptyId) {
        this.layoutEmptyId = layoutEmptyId;
    }

    public void layoutBizErrorId(@LayoutRes int layoutBizErrorId) {
        this.layoutBizErrorId = layoutBizErrorId;
    }

    public void layoutHttpErrorId(@LayoutRes int layoutHttpErrorId) {
        this.layoutHttpErrorId = layoutHttpErrorId;
    }

    public void layoutBackground(@ColorRes int color) {
        this.layoutBackground = color;
    }

    View getContentRootView() {
        return contentRootView;
    }


    void changeShowAnimation(@NonNull View view, boolean visible) {
        if (view == null) {
            return;
        }
        Animation anim;
        if (visible) {
            if (view.getVisibility() == View.VISIBLE) {
                return;
            }
            view.setVisibility(View.VISIBLE);
            anim = AnimationUtils.loadAnimation(baseView.activity(), android.R.anim.fade_in);
        } else {
            if (view.getVisibility() == View.GONE) {
                return;
            }
            view.setVisibility(View.GONE);
            anim = AnimationUtils.loadAnimation(baseView.activity(), android.R.anim.fade_out);
        }

        anim.setDuration(baseView.activity().getResources().getInteger(android.R.integer.config_shortAnimTime));
        view.startAnimation(anim);
    }

    void layoutContent() {
        if (layoutContent == null) {
            return;
        }
        changeShowAnimation(layoutLoading, false);
        changeShowAnimation(layoutEmpty, false);
        changeShowAnimation(layoutBizError, false);
        changeShowAnimation(layoutHttpError, false);

        if (layoutStateContent != null) {
            changeShowAnimation(layoutStateContent, true);
        } else {
            changeShowAnimation(layoutContent, true);
        }
        showState = IBaseView.STATE_CONTENT;
    }

    void layoutLoading() {
        if (layoutLoadingId < 1) {
            return;
        }
        changeShowAnimation(layoutEmpty, false);
        changeShowAnimation(layoutBizError, false);
        changeShowAnimation(layoutHttpError, false);
        if (layoutStateContent != null) {
            changeShowAnimation(layoutStateContent, false);
        } else {
            changeShowAnimation(layoutContent, false);
        }
        if (layoutLoading == null && vsLoading != null) {
            layoutLoading = vsLoading.inflate();
            BaseCheckUtils.checkNotNull(layoutLoading, "无法根据布局文件ID,获取layoutLoading");
        }
        changeShowAnimation(layoutLoading, true);
        showState = IBaseView.STATE_LOADING;
    }

    void layoutEmpty() {
        if (layoutEmpty == null) {
            return;
        }
        changeShowAnimation(layoutBizError, false);
        changeShowAnimation(layoutHttpError, false);
        if (layoutStateContent != null) {
            changeShowAnimation(layoutStateContent, false);
        } else {
            changeShowAnimation(layoutContent, false);
        }
        changeShowAnimation(layoutLoading, false);
        changeShowAnimation(layoutEmpty, true);
        showState = IBaseView.STATE_EMPTY;
    }

    void layoutBizError() {
        if (layoutBizError == null) {
            return;
        }
        changeShowAnimation(layoutEmpty, false);
        changeShowAnimation(layoutHttpError, false);
        if (layoutStateContent != null) {
            changeShowAnimation(layoutStateContent, false);
        } else {
            changeShowAnimation(layoutContent, false);
        }
        changeShowAnimation(layoutLoading, false);
        changeShowAnimation(layoutBizError, true);
        showState = IBaseView.STATE_BIZ_ERROR;
    }

    void layoutHttpError() {
        if (layoutHttpError == null) {
            return;
        }
        changeShowAnimation(layoutEmpty, false);
        changeShowAnimation(layoutBizError, false);
        if (layoutStateContent != null) {
            changeShowAnimation(layoutStateContent, false);
        } else {
            changeShowAnimation(layoutContent, false);
        }
        changeShowAnimation(layoutLoading, false);
        changeShowAnimation(layoutHttpError, true);
        showState = IBaseView.STATE_HTTP_ERROR;
    }

    int getLayoutState() {
        return showState;
    }

    private boolean isOpenSwipBackLayout;

    public void swipBackIsOpen(boolean isOpenSwipBackLayout) {
        this.isOpenSwipBackLayout = isOpenSwipBackLayout;
    }

    // 获取
    boolean isOpenSwipBackLayout() {
        return isOpenSwipBackLayout;
    }

    /**
     * TintManger
     */
    private int		tintColor;

    private boolean	statusBarEnabled			= true;

    private boolean	navigationBarTintEnabled	= true;

    private boolean	fitsSystem					= true;

    private boolean	tint;

    int getTintColor() {
        return tintColor;
    }

    boolean isTintColor() {
        return tintColor > 0;
    }

    public boolean isFitsSystem() {
        return fitsSystem;
    }

    public boolean getStatusBarTintEnabled() {
        return statusBarEnabled;
    }

    public boolean getNavigationBarTintEnabled() {
        return navigationBarTintEnabled;
    }

    public boolean isTint() {
        return tint;
    }

    public void tintIs(boolean isTint) {
        this.tint = isTint;
    }

    public void tintColor(@ColorRes int tintColor) {
        this.tintColor = tintColor;
    }

    public void tintStatusBarEnabled(boolean isStatusBar) {
        this.statusBarEnabled = isStatusBar;
    }

    public void tintNavigationBarEnabled(boolean isNavigationBar) {
        this.navigationBarTintEnabled = isNavigationBar;
    }

    public void tintFitsSystem(boolean isFitsSystem) {
        this.fitsSystem = isFitsSystem;
    }

    /**
     * actionbar
     */

    private Toolbar.OnMenuItemClickListener	menuListener;

    private int								toolbarLayoutId	= R.layout.sky_include_toolbar;

    private int								toolbarId		= R.id.toolbar;

    private int								toolbarMenuId;

    private boolean							isOpenToolbar;

    private boolean							isOpenCustomToolbar;

    private boolean							isOpenToolbarBack;

    // 获取
    int getToolbarLayoutId() {
        return toolbarLayoutId;
    }

    public boolean isOpenCustomToolbar() {
        return isOpenCustomToolbar;
    }

    boolean isOpenToolbar() {
        return isOpenToolbar;
    }

    boolean isOpenToolbarBack() {
        return isOpenToolbarBack;
    }

    int getToolbarId() {
        return toolbarId;
    }

    int getToolbarMenuId() {
        return toolbarMenuId;
    }

    @Nullable Toolbar.OnMenuItemClickListener getMenuListener() {
        return menuListener;
    }

    // 设置

    public void toolbarId(@IdRes int toolbarId) {
        this.toolbarId = toolbarId;
        this.isOpenCustomToolbar = true;
    }

    public void toolbarLayoutId(@LayoutRes int toolbarLayoutId) {
        this.isOpenToolbar = true;
        this.toolbarLayoutId = toolbarLayoutId;
    }

    public void toolbarMenuListener(@NonNull Toolbar.OnMenuItemClickListener menuListener) {
        this.menuListener = menuListener;
    }

    public void toolbarIsBack(@NonNull boolean isOpenToolbarBack) {
        this.isOpenToolbarBack = isOpenToolbarBack;
    }

    public void toolbarIsOpen(@NonNull boolean isOpenToolbar) {
        this.isOpenToolbar = isOpenToolbar;
    }

    public void toolbarMenuId(@MenuRes int toolbarMenuId) {
        this.toolbarMenuId = toolbarMenuId;
    }

    /**
     * RecyclerView 替代ListView GradView 可以实现瀑布流
     */

    private int															recyclerviewId;

    private int															recyclerviewColorResIds[];

    private int															recyclerviewSwipRefreshId;

    private BaseFooterListener SKYFooterListener;

    private RecyclerView recyclerView;

    private BaseRVAdapter BaseRVAdapter;

    private RecyclerView.LayoutManager									layoutManager;					// 布局管理器

    private RecyclerView.ItemAnimator									itemAnimator;					// 动画

    private RecyclerView.ItemDecoration									itemDecoration;					// 分割线

    private SwipeRefreshLayout recyclerviewSwipeContainer;

    private BaseRefreshListener recyclerviewSKYRefreshListener;

    private SwipeRefreshLayout.OnRefreshListener						onRefreshListener;

    private StickyRecyclerHeadersTouchListener.OnHeaderClickListener	onHeaderClickListener;

    private boolean														isHeaderFooter;

    int getRecyclerViewId() {
        return recyclerviewId;
    }

    RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Nullable BaseRVAdapter getBaseRVAdapterItem() {
        return BaseRVAdapter;
    }

    @Nullable public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    @Nullable RecyclerView.ItemAnimator getItemAnimator() {
        return itemAnimator;
    }

    @Nullable RecyclerView.ItemDecoration getItemDecoration() {
        return itemDecoration;
    }

    @Nullable SwipeRefreshLayout getSwipeContainer() {
        return recyclerviewSwipeContainer;
    }

    int[] getRecyclerviewColorResIds() {
        return recyclerviewColorResIds;
    }

    int getRecyclerviewSwipRefreshId() {
        return recyclerviewSwipRefreshId;
    }

    public void recyclerviewGridOpenHeaderFooter(boolean bool) {
        this.isHeaderFooter = bool;
    }

    // 设置
    public void recyclerviewId(@IdRes int recyclerviewId) {
        this.recyclerviewId = recyclerviewId;
    }

    public void recyclerviewLoadingMore(@NonNull BaseFooterListener SKYFooterListener) {
        this.SKYFooterListener = SKYFooterListener;
    }

    public void recyclerviewStickyHeaderClick(@NonNull StickyRecyclerHeadersTouchListener.OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }

    public void recyclerviewAdapter(@NonNull BaseRVAdapter SKYRVAdapter) {
        this.BaseRVAdapter = SKYRVAdapter;
    }

    public void recyclerviewGridManager(@NonNull GridLayoutManager gridLayoutManager) {
        this.layoutManager = gridLayoutManager;
    }

    public void recyclerviewLinearManager(@NonNull LinearLayoutManager linearLayoutManager) {
        this.layoutManager = linearLayoutManager;
    }

    public void recyclerviewAnimator(@NonNull RecyclerView.ItemAnimator itemAnimator) {
        this.itemAnimator = itemAnimator;
    }

    public void recyclerviewLinearLayoutManager(int direction, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
        boolean reverse = false;
        if (reverseLayout != null && reverseLayout.length > 0) {
            reverse = reverseLayout[0];
        }
        this.layoutManager = new LinearLayoutManager(baseView.activity(), direction, reverse);
        this.itemDecoration = itemDecoration;
        this.itemAnimator = itemAnimator;
    }

    public void recyclerviewGridLayoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
        boolean reverse = false;
        if (reverseLayout != null && reverseLayout.length > 0) {
            reverse = reverseLayout[0];
        }
        this.layoutManager = new GridLayoutManager(baseView.activity(), spanCount, direction, reverse);
        this.itemDecoration = itemDecoration;
        this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
    }

    public void recyclerviewStaggeredGridyoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
        this.layoutManager = new StaggeredGridLayoutManager(spanCount, direction);
        this.itemDecoration = itemDecoration;
        this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
    }

    public void recyclerviewColorResIds(int... recyclerviewColorResIds) {
        this.recyclerviewColorResIds = recyclerviewColorResIds;
    }

    public void recyclerviewSwipRefreshId(@IdRes int recyclerviewSwipRefreshId, @NonNull BaseRefreshListener recyclerviewSKYRefreshListener) {
        this.recyclerviewSwipRefreshId = recyclerviewSwipRefreshId;
        this.recyclerviewSKYRefreshListener = recyclerviewSKYRefreshListener;
    }

    public void recyclerviewSwipRefreshId(@IdRes int recyclerviewSwipRefreshId, @NonNull SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.recyclerviewSwipRefreshId = recyclerviewSwipRefreshId;
        this.onRefreshListener = onRefreshListener;
    }

    public void recyclerRefreshing(boolean bool) {
        if (recyclerviewSwipeContainer != null) {
            recyclerviewSwipeContainer.setRefreshing(bool);
        }
    }

    /**
     * 创建
     *
     * @return
     */
    View create() {
        /** layout **/
        createLayout();
        /** recyclerview **/
        createRecyclerView(contentRoot);
        /** actoinbar **/
        contentRootView = createActionbar(contentRoot);
        /** background color **/
        if (layoutBackground != 0) {
            contentRootView.setBackgroundResource(layoutBackground);
        }
        return contentRootView;
    }

    /**
     * 清空所有
     */
    void detach() {
        // 清楚
        if (baseView != null) {
            baseView.detach();
            baseView = null;
        }
        // 基础清除
        detachLayout();
        // actionbar清除
        detachActionbar();
        // recyclerView清除
        detachRecyclerView();
    }

    /**
     * 布局
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private void createLayout() {
        contentRoot = new FrameLayout(baseView.context());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        // 内容
        if (getLayoutId() > 0) {
            layoutContent = mInflater.inflate(getLayoutId(), null, false);
            if (layoutStateId > 0) {
                ViewGroup view = (ViewGroup) layoutContent.findViewById(layoutStateId);
                layoutStateContent = view.getChildAt(0);
                if (layoutStateContent == null) {
                    BaseCheckUtils.checkNotNull(layoutContent, "指定切换状态布局后,内容不能为空");
                }
            }
            BaseCheckUtils.checkNotNull(layoutContent, "无法根据布局文件ID,获取layoutContent");
            contentRoot.addView(layoutContent, layoutParams);
        }

        // 进度条
        layoutLoadingId = layoutLoadingId > 0 ? layoutLoadingId : BaseHelper.getCommonView() == null ? 0 : BaseHelper.getCommonView().layoutLoading();
        if (layoutLoadingId > 0) {
            vsLoading = new ViewStub(baseView.activity());
            vsLoading.setLayoutResource(layoutLoadingId);

            if (layoutStateId > 0) {
                ViewGroup view = (ViewGroup) layoutContent.findViewById(layoutStateId);
                view.addView(vsLoading, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                contentRoot.addView(vsLoading, layoutParams);
            }
        }

        // 空布局
        layoutEmptyId = layoutEmptyId > 0 ? layoutEmptyId : BaseHelper.getCommonView() == null ? 0 : BaseHelper.getCommonView().layoutEmpty();
        if (layoutEmptyId > 0) {
            layoutEmpty = mInflater.inflate(layoutEmptyId, null, false);
            BaseCheckUtils.checkNotNull(layoutEmpty, "无法根据布局文件ID,获取layoutEmpty");

            if (layoutStateId > 0) {
                ViewGroup view = (ViewGroup) layoutContent.findViewById(layoutStateId);
                view.addView(layoutEmpty, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                contentRoot.addView(layoutEmpty, layoutParams);
            }
            layoutEmpty.setVisibility(View.GONE);
        }

        // 业务错误布局
        layoutBizErrorId = layoutBizErrorId > 0 ? layoutBizErrorId : BaseHelper.getCommonView() == null ? 0 : BaseHelper.getCommonView().layoutBizError();
        if (layoutBizErrorId > 0) {
            layoutBizError = mInflater.inflate(layoutBizErrorId, null, false);
            BaseCheckUtils.checkNotNull(layoutBizError, "无法根据布局文件ID,获取layoutBizError");
            if (layoutStateId > 0) {
                ViewGroup view = (ViewGroup) layoutContent.findViewById(layoutStateId);
                view.addView(layoutBizError, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                contentRoot.addView(layoutBizError, layoutParams);
            }
            layoutBizError.setVisibility(View.GONE);
        }

        // 网络错误布局
        layoutHttpErrorId = layoutHttpErrorId > 0 ? layoutHttpErrorId : BaseHelper.getCommonView() == null ? 0 : BaseHelper.getCommonView().layoutHttpError();
        if (layoutHttpErrorId > 0) {
            BaseCheckUtils.checkArgument(layoutHttpErrorId > 0, "网络错误布局Id不能为空,重写公共布局Application.layoutBizError 或者 在Buider.layout里设置");
            layoutHttpError = mInflater.inflate(layoutHttpErrorId, null, false);
            BaseCheckUtils.checkNotNull(layoutHttpError, "无法根据布局文件ID,获取layoutHttpError");
            if (layoutStateId > 0) {
                ViewGroup view = (ViewGroup) layoutContent.findViewById(layoutStateId);
                view.addView(layoutHttpError, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                contentRoot.addView(layoutHttpError, layoutParams);
            }
            layoutHttpError.setVisibility(View.GONE);
        }
    }

    private void detachLayout() {
        contentRootView = null;
        contentRoot = null;
        mInflater = null;
        layoutContent = null;
        layoutBizError = null;
        layoutHttpError = null;
        layoutEmpty = null;
        vsLoading = null;
        layoutLoading = null;
    }

    /**
     * 标题栏
     *
     * @param view
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private View createActionbar(View view) {
        if (isOpenToolbar()) {
            final RelativeLayout toolbarRoot = new RelativeLayout(baseView.context());
            toolbarRoot.setId(R.id.sky_home);
            toolbarRoot.setFitsSystemWindows(fitsSystem);
            // 添加toolbar布局
            mInflater.inflate(getToolbarLayoutId(), toolbarRoot, true);
            // 添加内容布局
            RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            contentLayoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
            toolbarRoot.addView(view, contentLayoutParams);
            toolbar = ButterKnife.findById(toolbarRoot, getToolbarId());

            BaseCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");

            // 添加点击事件
            if (getMenuListener() != null) {
                toolbar.setOnMenuItemClickListener(getMenuListener());
            }
            if (getToolbarMenuId() > 0) {
                toolbar.inflateMenu(getToolbarMenuId());
            }
            if (isOpenToolbarBack()) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override public void onClick(View v) {
                        BaseKeyboardUtils.hideSoftInput(baseView.activity());
                        switch (baseView.getState()) {
                            case BaseView.STATE_ACTIVITY:
                                baseView.activity().onKeyBack();
                                break;
                            case BaseView.STATE_FRAGMENT:
                                baseView.fragment().onKeyBack();
                                break;
                            case BaseView.STATE_DIALOGFRAGMENT:
                                baseView.dialogFragment().onKeyBack();
                                break;
                        }
                    }
                });
            } else {
            }

            toolbarRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            return toolbarRoot;
        } else if (isOpenCustomToolbar()) {
            view.setId(R.id.sky_home);
            view.setFitsSystemWindows(fitsSystem);
            toolbar = ButterKnife.findById(view, getToolbarId());

            BaseCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");

            if (isOpenToolbarBack()) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override public void onClick(View v) {
                        BaseKeyboardUtils.hideSoftInput(baseView.activity());
                        switch (baseView.getState()) {
                            case BaseView.STATE_ACTIVITY:
                                baseView.activity().onKeyBack();
                                break;
                            case BaseView.STATE_FRAGMENT:
                                baseView.fragment().onKeyBack();
                                break;
                            case BaseView.STATE_DIALOGFRAGMENT:
                                baseView.dialogFragment().onKeyBack();
                                break;
                        }
                    }
                });
            }
            // 添加点击事件
            if (getMenuListener() != null) {
                toolbar.setOnMenuItemClickListener(getMenuListener());
            }
            if (getToolbarMenuId() > 0) {
                toolbar.inflateMenu(getToolbarMenuId());
            }

            return view;
        } else {
            view.setId(R.id.sky_home);
            view.setFitsSystemWindows(fitsSystem);
            return view;
        }
    }

    private void detachActionbar() {
        menuListener = null;
        toolbar = null;
        menuListener = null;
    }

    /**
     * 列表
     *
     * @param view
     */
    private void createRecyclerView(View view) {
        if (getRecyclerViewId() > 0) {
            recyclerView = ButterKnife.findById(view, getRecyclerViewId());
            BaseCheckUtils.checkNotNull(recyclerView, "无法根据布局文件ID,获取recyclerView");
            BaseCheckUtils.checkNotNull(layoutManager, "LayoutManger不能为空");
            recyclerView.setLayoutManager(layoutManager);
            if (BaseRVAdapter != null) {
                // 扩展适配器
                if (BaseRVAdapter instanceof BaseStickyHeaders) {
                    BaseStickyHeaders SKYStickyHeaders = (BaseStickyHeaders) BaseRVAdapter;
                    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(SKYStickyHeaders);
                    recyclerView.addItemDecoration(headersDecor);

                    if (onHeaderClickListener != null) {
                        StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
                        touchListener.setOnHeaderClickListener(onHeaderClickListener);
                        recyclerView.addOnItemTouchListener(touchListener);

                    }
                    BaseRVAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                        @Override public void onChanged() {
                            headersDecor.invalidateHeaders();
                        }
                    });
                }
                recyclerView.setAdapter(BaseRVAdapter);
                if (isHeaderFooter) {
                    final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    BaseCheckUtils.checkNotNull(gridLayoutManager, "LayoutManger，不是GridLayoutManager");
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                        @Override public int getSpanSize(int position) {
                            return BaseRVAdapter.isHeaderAndFooter(position) ? gridLayoutManager.getSpanCount() : 1;
                        }
                    });
                }
                // 设置Item增加、移除动画
                recyclerView.setItemAnimator(getItemAnimator());
                // 添加分割线
                if (getItemDecoration() != null) {
                    recyclerView.addItemDecoration(getItemDecoration());
                }
                // 优化
                recyclerView.setHasFixedSize(true);
                // 设置上拉和下拉事件
                if (getRecyclerviewSwipRefreshId() != 0) {
                    recyclerviewSwipeContainer = ButterKnife.findById(view, getRecyclerviewSwipRefreshId());
                    BaseCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");

                    if (onRefreshListener != null) {
                        recyclerviewSwipeContainer.setOnRefreshListener(onRefreshListener);
                    } else if (recyclerviewSKYRefreshListener != null) {
                        recyclerView.addOnScrollListener(new BaseOnScrollListener(recyclerviewSKYRefreshListener));// 加载更多
                        recyclerviewSwipeContainer.setOnRefreshListener(recyclerviewSKYRefreshListener);// 下载刷新
                    }
                } else {
                    if (SKYFooterListener != null) {
                        recyclerView.addOnScrollListener(new BaseFooterOnScrollListener(SKYFooterListener));// 加载更多

                    }
                }
            } else {
                BaseCheckUtils.checkNotNull(null, "SKYRVAdapter适配器不能为空");
            }

            // 设置进度颜色
            if (getRecyclerviewColorResIds() != null) {
                BaseCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
                recyclerviewSwipeContainer.setColorSchemeResources(getRecyclerviewColorResIds());
            }
        }
    }

    private void detachRecyclerView() {
        recyclerView = null;
        if (BaseRVAdapter != null) {
            BaseRVAdapter.clearCache();
            BaseRVAdapter = null;
        }
        onHeaderClickListener = null;
        layoutManager = null;
        itemAnimator = null;
        itemDecoration = null;
        recyclerviewSwipeContainer = null;
        recyclerviewSKYRefreshListener = null;
    }


}



















