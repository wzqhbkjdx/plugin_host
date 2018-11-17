package cent.news.com.baseframe.modules.threadPool;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseWorkExecutorService extends ThreadPoolExecutor {

    BaseWorkExecutorService() {
        super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

}
