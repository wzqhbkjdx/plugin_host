package cent.news.com.baseframe.view.adapter.recyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import cent.news.com.baseframe.view.common.BaseFooterListener;

public class BaseFooterOnScrollListener extends RecyclerView.OnScrollListener {


    public BaseFooterOnScrollListener(BaseFooterListener footerListener) {
        this.footerListener = footerListener;
    }

    public void setRecyclerviewSKYRefreshListener(BaseFooterListener footerListener) {
        this.footerListener = footerListener;
    }

    private BaseFooterListener	footerListener;

    private int					mLoadMoreRequestedItemCount;

    private boolean				mLoadMoreIsAtBottom;

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
            if (footerListener.onScrolledToBottom()) {
                mLoadMoreRequestedItemCount = recyclerView.getAdapter().getItemCount();
                mLoadMoreIsAtBottom = false;
            }
        }
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (recyclerView == null) {
            return;
        }
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            mLoadMoreIsAtBottom = recyclerView.getAdapter().getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager());
            //获取最后一个完全显示的ItemPosition
            int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
            int lastVisiblePos = getMaxElem(lastVisiblePositions);
            int totalItemCount = manager.getItemCount();
            // 判断是否滚动到底部
            mLoadMoreIsAtBottom = lastVisiblePos == (totalItemCount -1);
        }
    }
    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i] > maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }
}
