package cent.news.com.baseframe.core.plugin;

import android.os.Bundle;

import cent.news.com.baseframe.view.BaseFragment;

/**
 * Created by bym on 2018/6/19.
 */

public interface BaseFragmentInterceptor {

    void onFragmentCreated(BaseFragment SKYFragment, Bundle bundle, Bundle savedInstanceState);

    void buildAfter(BaseFragment SKYFragment);

    void onFragmentStart(BaseFragment SKYFragment);

    void onFragmentResume(BaseFragment SKYFragment);

    void onFragmentPause(BaseFragment SKYFragment);

    void onFragmentStop(BaseFragment SKYFragment);

    void onFragmentDestroy(BaseFragment SKYFragment);

    BaseFragmentInterceptor NONE = new BaseFragmentInterceptor() {

        @Override public void onFragmentCreated(BaseFragment SKYFragment, Bundle bundle, Bundle savedInstanceState) {

        }

        @Override public void buildAfter(BaseFragment SKYFragment) {

        }

        @Override public void onFragmentStart(BaseFragment SKYFragment) {

        }

        @Override public void onFragmentResume(BaseFragment SKYFragment) {

        }

        @Override public void onFragmentPause(BaseFragment SKYFragment) {

        }

        @Override public void onFragmentStop(BaseFragment SKYFragment) {

        }

        @Override public void onFragmentDestroy(BaseFragment SKYFragment) {

        }
    };

}
