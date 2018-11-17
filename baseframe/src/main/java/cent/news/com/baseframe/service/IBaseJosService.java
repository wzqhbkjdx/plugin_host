package cent.news.com.baseframe.service;

import android.app.job.JobInfo;

public interface IBaseJosService {

    void schedule(JobInfo.Builder builder);

    void cancel(int id);

    void cancelAll();

    JobInfo.Builder builder(int id, Class clazz);

}
