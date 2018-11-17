package cent.news.com.baseframe.base;

import cent.news.com.baseframe.view.adapter.recyclerView.BaseRVAdapter;

/**
 * Created by bym on 2018/6/16.
 */

public interface IBaseView {

    int	STATE_CONTENT		= 1;

    int	STATE_LOADING		= 2;

    int	STATE_EMPTY			= 3;

    int	STATE_BIZ_ERROR		= 4;

    int	STATE_HTTP_ERROR	= 5;

    void showContent();

    void showEmpty();

    void showBizError();

    void showLoading();

    void showHttpError();

    int showState();

    <T extends BaseRVAdapter> T adapter();


}
