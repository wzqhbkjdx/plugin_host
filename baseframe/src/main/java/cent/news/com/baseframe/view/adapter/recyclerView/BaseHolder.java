package cent.news.com.baseframe.view.adapter.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by bym on 2018/6/19.
 */

public abstract class BaseHolder<T> extends RecyclerView.ViewHolder {
    private BaseRVAdapter adapter;

    public BaseHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bindData(T t, int position);

    public void setAdapter(BaseRVAdapter adapter) {
        this. adapter = adapter;
    }

    public <T extends BaseRVAdapter> T getAdapter() {
        return (T) adapter;
    }
}
