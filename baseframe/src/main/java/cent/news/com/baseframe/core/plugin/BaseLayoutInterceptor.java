package cent.news.com.baseframe.core.plugin;

import cent.news.com.baseframe.view.BaseActivity;
import cent.news.com.baseframe.view.BaseFragment;

/**
 * Created by bym on 2018/6/19.
 */

public interface BaseLayoutInterceptor {

    BaseLayoutInterceptor NONE = new BaseLayoutInterceptor() {

        @Override public void showContent(BaseActivity skyActivity) {

        }

        @Override public void showEmpty(BaseActivity skyActivity) {

        }

        @Override public void showBizError(BaseActivity skyActivity) {

        }

        @Override public void showLoading(BaseActivity skyActivity) {

        }

        @Override public void showHttpError(BaseActivity skyActivity) {

        }

        @Override public void showContent(BaseFragment skyFragment) {

        }

        @Override public void showEmpty(BaseFragment skyFragment) {

        }

        @Override public void showBizError(BaseFragment skyFragment) {

        }

        @Override public void showLoading(BaseFragment skyFragment) {

        }

        @Override public void showHttpError(BaseFragment skyFragment) {

        }

    };

    void showContent(BaseActivity skyActivity);

    void showEmpty(BaseActivity skyActivity);

    void showBizError(BaseActivity skyActivity);

    void showLoading(BaseActivity skyActivity);

    void showHttpError(BaseActivity skyActivity);

    void showContent(BaseFragment skyFragment);

    void showEmpty(BaseFragment skyFragment);

    void showBizError(BaseFragment skyFragment);

    void showLoading(BaseFragment skyFragment);

    void showHttpError(BaseFragment skyFragment);


}
