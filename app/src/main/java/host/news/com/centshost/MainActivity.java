package host.news.com.centshost;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import cent.news.com.baseframe.view.BaseActivity;
import cent.news.com.baseframe.view.BaseBuilder;

public class MainActivity extends BaseActivity<MainBiz> {

    private static final String pluginActivityName = "cent.news.com.newscent.MainActivity";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tv_welcome)
    TextView welcome;

    @Override
    protected BaseBuilder build(BaseBuilder builder) {
        builder.layoutId(R.layout.activity_main);
        return builder;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        biz().installExternalPlugin("app-debug.apk");
    }

    public void pluginInstallFinish() {
        welcome.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        biz().startPluginActivity(this, pluginActivityName);
    }
}
