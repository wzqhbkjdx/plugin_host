package host.news.com.centshost;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.qihoo360.replugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cent.news.com.baseframe.core.BaseBiz;
import host.news.com.centshost.Helper.HostHelper;
import sky.Background;
import sky.BackgroundType;

/**
 * Created by bym on 2018/11/17.
 */

public class MainBiz extends BaseBiz<MainActivity> {

    private PluginInfo info = null;

    @Override
    protected void initBiz(Bundle bundle) {
        super.initBiz(bundle);
    }

    @Background(BackgroundType.WORK)
    public void installExternalPlugin(String apkName) {
        String demo3apkPath = "external" + File.separator + apkName;

        // 文件是否已经存在？直接删除重来
        String pluginFilePath = HostHelper.getInstance().getFilesDir().getAbsolutePath() + File.separator + apkName;
        File pluginFile = new File(pluginFilePath);
        if (pluginFile.exists()) {
            FileUtils.deleteQuietly(pluginFile);
        }

        // 开始复制
        copyAssetsFileToAppFiles(demo3apkPath, apkName);
        if (pluginFile.exists()) {
            info = RePlugin.install(pluginFilePath);
        }

        if(info != null) {
            ui().pluginInstallFinish();
        }
    }


    public void startPluginActivity(Context context, String pluginName) {
        if (info != null) {
            RePlugin.startActivity(context, RePlugin.createIntent(info.getName(), pluginName));
        } else {
            Toast.makeText(context, "install external plugin failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void copyAssetsFileToAppFiles(String assetFileName, String newFileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        int buffSize = 1024;

        try {
            is =  HostHelper.getInstance().getAssets().open(assetFileName);
            fos = HostHelper.getInstance().openFileOutput(newFileName, Context.MODE_PRIVATE);
            int byteCount = 0;
            byte[] buffer = new byte[buffSize];
            while((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
