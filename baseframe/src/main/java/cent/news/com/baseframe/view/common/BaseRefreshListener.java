package cent.news.com.baseframe.view.common;

import android.support.v4.widget.SwipeRefreshLayout;

public interface BaseRefreshListener extends SwipeRefreshLayout.OnRefreshListener {

    boolean onScrolledToBottom();

}
