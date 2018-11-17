package cent.news.com.baseframe.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.modules.log.L;

public class InnerJobService implements IBaseJosService {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void schedule(JobInfo.Builder builder) {
        JobScheduler jobScheduler = (JobScheduler) BaseHelper.getInstance().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            if (BaseHelper.isLogOpen()) {
                L.tag("SKYJobService");
                L.i("无法获取JobScheduler实例~");
            }
            return;
        }
        jobScheduler.schedule(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void cancel(int id) {
        JobScheduler jobScheduler = (JobScheduler) BaseHelper.getInstance().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            if (BaseHelper.isLogOpen()) {
                L.tag("SKYJobService");
                L.i("无法获取JobScheduler实例~");
            }
            return;
        }
        jobScheduler.cancel(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void cancelAll() {
        JobScheduler jobScheduler = (JobScheduler) BaseHelper.getInstance().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            if (BaseHelper.isLogOpen()) {
                L.tag("SKYJobService");
                L.i("无法获取JobScheduler实例~");
            }
            return;
        }
        jobScheduler.cancelAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public JobInfo.Builder builder(int id, Class clazz) {
        JobInfo.Builder builder = new JobInfo.Builder(id, new ComponentName(BaseHelper.getInstance().getPackageName(), clazz.getName()));
        return builder;
    }
}
