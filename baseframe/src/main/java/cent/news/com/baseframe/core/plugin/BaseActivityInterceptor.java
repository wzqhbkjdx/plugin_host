package cent.news.com.baseframe.core.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import cent.news.com.baseframe.view.BaseActivity;
import cent.news.com.baseframe.view.BaseBuilder;

/**
 * Created by bym on 2018/6/19.
 */

public interface BaseActivityInterceptor {

    void build(BaseActivity SKYIView, BaseBuilder initialSKYBuilder);

    void onCreate(BaseActivity SKYIView, Bundle bundle, Bundle savedInstanceState);

    void onPostCreate(BaseActivity SKYIView, Bundle savedInstanceState);

    void onStart(BaseActivity SKYIView);

    void onResume(BaseActivity SKYIView);

    void onPostResume(BaseActivity SKYIView);

    void onPause(BaseActivity SKYIView);

    void onStop(BaseActivity SKYIView);

    void onDestroy(BaseActivity SKYIView);

    void onRestart(BaseActivity SKYIView);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    BaseActivityInterceptor NONE = new BaseActivityInterceptor() {

        @Override
        public void build(BaseActivity SKYIView, BaseBuilder initialSKYBuilder) {

        }

        @Override public void onCreate(BaseActivity SKYIView, Bundle bundle, Bundle savedInstanceState) {

        }

        @Override public void onPostCreate(BaseActivity SKYIView, Bundle savedInstanceState) {

        }

        @Override public void onStart(BaseActivity SKYIView) {

        }

        @Override public void onResume(BaseActivity SKYIView) {

        }

        @Override public void onPostResume(BaseActivity SKYIView) {

        }

        @Override public void onPause(BaseActivity SKYIView) {

        }

        @Override public void onStop(BaseActivity SKYIView) {

        }

        @Override public void onDestroy(BaseActivity SKYIView) {

        }

        @Override public void onRestart(BaseActivity SKYIView) {

        }

        @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        }
    };


}
