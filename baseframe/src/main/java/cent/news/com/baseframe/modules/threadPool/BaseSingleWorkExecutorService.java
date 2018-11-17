package cent.news.com.baseframe.modules.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseSingleWorkExecutorService extends ThreadPoolExecutor {

    BaseSingleWorkExecutorService() {
        super(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

}
