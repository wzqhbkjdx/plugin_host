package cent.news.com.baseframe.modules.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by bym on 2018/6/19.
 */

public class BaseHttpExecutorService extends ThreadPoolExecutor {

    private static final int	DEFAULT_THREAD_COUNT	= 5;

    BaseHttpExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

}
