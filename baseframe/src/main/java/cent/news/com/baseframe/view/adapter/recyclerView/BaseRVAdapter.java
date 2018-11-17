package cent.news.com.baseframe.view.adapter.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.utils.BaseCheckUtils;
import cent.news.com.baseframe.view.BaseActivity;
import cent.news.com.baseframe.view.BaseDialogFragment;
import cent.news.com.baseframe.view.BaseFragment;
import cent.news.com.baseframe.view.BaseView;

/**
 * Created by bym on 2018/6/18.
 */

public abstract class BaseRVAdapter<T, V extends BaseHolder> extends RecyclerView.Adapter<V> implements BaseIRefresh {

    private static final int	VIEW_ITEM	= 0;

    private static final int	VIEW_PROG	= 9999;

    private static final int	VIEW_TOP	= 10000;

    public abstract V newViewHolder(ViewGroup viewGroup, int type);

    public V newLoadMoreHolder(ViewGroup viewGroup, int type) {
        return null;
    }

    public V newTopHolder(ViewGroup viewGroup, int type) {
        return null;
    }

    private BaseRVAdapter() {}

    /**
     * 数据
     */
    private List mItems;

    private BaseView baseView;

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BaseRVAdapter(BaseActivity BaseActivity) {
        BaseCheckUtils.checkNotNull(BaseActivity, "View层不存在");
        this.baseView = BaseActivity.baseView();
    }

    public BaseRVAdapter(BaseFragment baseFragment) {
        BaseCheckUtils.checkNotNull(baseFragment, "View层不存在");
        this.baseView = baseFragment.baseView();
    }

    public BaseRVAdapter(BaseDialogFragment baseDialogFragment) {
        BaseCheckUtils.checkNotNull(baseDialogFragment, "View层不存在");
        this.baseView = baseDialogFragment.baseView();
    }

    @Override public int getItemViewType(int position) {
        if(mItems == null || mItems.size() < 1){
            return getCustomViewType(position);
        }
        if (position == 0) {
            return mItems.get(position) != null ? getCustomViewType(position) : VIEW_TOP;
        } else {
            return mItems.get(position) != null ? getCustomViewType(position) : VIEW_PROG;
        }
    }

    public int getCustomViewType(int position) {
        return VIEW_ITEM;
    }

    protected View inflate(ViewGroup viewGroup, int layout){
        return LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
    }

    @Override public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        V holder;
        if (viewType == VIEW_PROG) {
            holder = newLoadMoreHolder(viewGroup, viewType);
            holder.setAdapter(this);
        } else if (viewType == VIEW_TOP) {
            holder = newTopHolder(viewGroup, viewType);
            holder.setAdapter(this);
        } else {
            holder = newViewHolder(viewGroup, viewType);
            holder.setAdapter(this);
        }
        return holder;
    }


    @Override public void onBindViewHolder(V v, int position) {
        v.bindData(getItem(position), position);
    }

    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void add(int position, Object object) {
        if (object == null || getItems() == null || position < 0 || position > getItems().size()) {
            return;
        }
        mItems.add(position, object);
        notifyItemInserted(position);
    }

    public void add(Object object) {
        if (object == null || getItems() == null) {
            return;
        }
        mItems.add(object);
        notifyItemInserted(mItems.size());

    }

    public void addList(int position, List list) {
        if (list == null || list.size() < 1 || getItems() == null || position < 0 || position > getItems().size()) {
            return;
        }
        mItems.addAll(position, list);
        notifyItemRangeInserted(position, list.size());

    }

    public void addList(List list) {
        if (list == null || list.size() < 1 || getItems() == null) {
            return;
        }
        int postion = getItemCount();
        mItems.addAll(list);
        notifyItemRangeInserted(postion, list.size());
    }

    public void delete(int position) {
        if (getItems() == null || position < 0 || getItems().size() < position) {
            return;
        }
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void delete(List list) {
        if (list == null || list.size() < 1 || getItems() == null) {
            return;
        }
        int position = getItemCount();
        mItems.removeAll(list);
        notifyItemRangeRemoved(position, list.size());
    }

    public void delete(int position, List list) {
        if (list == null || list.size() < 1 || getItems() == null) {
            return;
        }
        mItems.removeAll(list);
        notifyItemRangeRemoved(position, list.size());
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return (T) mItems.get(position);
    }

    public void updateData() {
        notifyDataSetChanged();
    }

    public <V extends BaseFragment> V fragment() {
        return baseView.fragment();
    }

    public <A extends BaseActivity> A activity() {
        return baseView.activity();
    }

    public <D extends BaseDialogFragment> D dialogFragment() {
        return baseView.dialogFragment();
    }

    /**
     * 获取适配器
     *
     * @return 返回值
     */
    protected BaseRVAdapter getAdapter() {
        return this;
    }

    /**
     * 获取fragment
     *
     * @param <T>
     *            参数
     * @param clazz
     *            参数
     * @return 返回值
     */
    public <T> T findFragment(Class<T> clazz) {
        BaseCheckUtils.checkNotNull(clazz, "class不能为空");
        return (T) baseView.manager().findFragmentByTag(clazz.getSimpleName());
    }

    public BaseView getUI() {
        return baseView;
    }

    public <B extends IBaseBiz> B biz(Class<B> service) {
        return baseView.biz(service);
    }

    /**
     * 获取调度
     *
     * @param e
     *            参数
     * @param <E>
     *            参数
     * @return 返回值
     */
    protected <E extends BaseIDisplay> E display(Class<E> e) {
        return baseView.display(e);
    }

    @Override public int getItemCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    public boolean isHeaderAndFooter(int position) {
        return false;
    }

    public void clearCache() {}

    @Override public void notify(Object object) {

    }
}













