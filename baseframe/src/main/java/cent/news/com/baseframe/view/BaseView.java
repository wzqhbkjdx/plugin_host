package cent.news.com.baseframe.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.utils.BaseCheckUtils;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseView {

    /**
     * 常量
     */
    public static final int		STATE_ACTIVITY			= 99999;

    public static final int		STATE_FRAGMENT			= 88888;

    public static final int		STATE_DIALOGFRAGMENT	= 77777;

    public static final int		STATE_NOTVIEW			= 66666;

    /** 类型 **/
    private int					state;

    private BaseActivity mSKYActivity;

    private Context context;

    private BaseFragment			mSKYFragment;

    private BaseDialogFragment	mSKYDialogFragment;

    private FragmentManager fragmentManager;

    /**
     * 初始化
     *
     * @param mSKYActivity
     *            参数
     **/
    public void initUI(BaseActivity mSKYActivity) {
        this.state = STATE_ACTIVITY;
        this.mSKYActivity = mSKYActivity;
        this.context = mSKYActivity;
    }

    public void initUI(BaseFragment mSKYFragment) {
        initUI((BaseActivity) mSKYFragment.getActivity());
        this.state = STATE_FRAGMENT;
        this.mSKYFragment = mSKYFragment;
    }

    public void initUI(BaseDialogFragment mSKYDialogFragment) {
        initUI((BaseActivity) mSKYDialogFragment.getActivity());
        this.state = STATE_DIALOGFRAGMENT;
        this.mSKYDialogFragment = mSKYDialogFragment;
    }

    public void initUI(Context context) {
        this.context = context;
        this.state = STATE_NOTVIEW;
    }

    public Context context() {
        return context;
    }

    public <A extends BaseActivity> A activity() {
        return (A) mSKYActivity;
    }

    public FragmentManager manager() {
        return BaseHelper.screenHelper().getCurrentActivity().getSupportFragmentManager();
    }

    public Object getView() {
        Object obj = null;
        switch (state) {
            case STATE_ACTIVITY:
                obj = mSKYActivity;
                break;
            case STATE_FRAGMENT:
                obj = mSKYFragment;
                break;
            case STATE_DIALOGFRAGMENT:
                obj = mSKYDialogFragment;
                break;
        }
        return obj;
    }

    public int getState() {
        return state;
    }

    public <F extends BaseFragment> F fragment() {
        return (F) mSKYFragment;
    }

    public <D extends BaseDialogFragment> D dialogFragment() {
        return (D) mSKYDialogFragment;
    }

    public <B extends IBaseBiz> B biz() {
        B b = null;
        switch (state) {
            case STATE_ACTIVITY:
                b = (B) mSKYActivity.biz();
                break;
            case STATE_FRAGMENT:
                b = (B) mSKYFragment.biz();
                break;
            case STATE_DIALOGFRAGMENT:
                b = (B) mSKYDialogFragment.biz();
                break;
        }
        return b;
    }

    public <B extends IBaseBiz> B biz(Class<B> service) {
        B b = null;
        switch (state) {
            case STATE_ACTIVITY:
                b = (B) mSKYActivity.biz(service);
                break;
            case STATE_FRAGMENT:
                b = (B) mSKYFragment.biz(service);
                break;
            case STATE_DIALOGFRAGMENT:
                b = (B) mSKYDialogFragment.biz(service);
                break;
        }
        return b;
    }

    public <E extends BaseIDisplay> E display(Class<E> display) {
        E e = null;
        switch (state) {
            case STATE_ACTIVITY:
                e = (E) mSKYActivity.display(display);
                break;
            case STATE_FRAGMENT:
                e = (E) mSKYFragment.display(display);
                break;
            case STATE_DIALOGFRAGMENT:
                e = (E) mSKYDialogFragment.display(display);
                break;
        }
        return e;
    }

    public Toolbar toolbar(int... types) {
        int type = state;
        if (types.length > 0) {
            type = types[0];
        }
        Toolbar toolbar = null;
        switch (type) {
            case STATE_ACTIVITY:
                toolbar = mSKYActivity.toolbar();
                break;
            case STATE_FRAGMENT:
                toolbar = mSKYFragment.toolbar();
                toolbar = toolbar == null ? mSKYActivity.toolbar() : toolbar;
                break;
            case STATE_DIALOGFRAGMENT:
                toolbar = mSKYDialogFragment.toolbar();
                toolbar = toolbar == null ? mSKYActivity.toolbar() : toolbar;
                break;
        }

        BaseCheckUtils.checkNotNull(toolbar, "标题栏没有打开，无法调用");
        return toolbar;
    }

    /**
     * 消除引用
     */
    public void detach() {
        this.state = 0;
        this.mSKYActivity = null;
        this.mSKYFragment = null;
        this.mSKYDialogFragment = null;
        this.context = null;
        this.fragmentManager = null;
    }

}









