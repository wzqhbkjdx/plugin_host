package cent.news.com.baseframe.modules.threadPool;

import java.util.concurrent.ExecutorService;

/**
 * Created by bym on 2018/6/19.
 */

public final class BaseThreadPoolManager {

    /** 线程服务-网络线程池 **/
    private BaseHttpExecutorService baseHttpExecutorService;

    /** 线程服务-并行工作线程池 **/
    private BaseWorkExecutorService baseWorkExecutorService;

    /** 线程服务-串行工作线程池 **/
    private BaseSingleWorkExecutorService baseSingleWorkExecutorService;

    public synchronized ExecutorService getHttpExecutorService() {
        if (baseHttpExecutorService == null) {
            baseHttpExecutorService = new BaseHttpExecutorService();
        }
        return baseHttpExecutorService;
    }

    public synchronized ExecutorService getSingleWorkExecutorService() {
        if (baseSingleWorkExecutorService == null) {
            baseSingleWorkExecutorService = new BaseSingleWorkExecutorService();
        }
        return baseSingleWorkExecutorService;
    }

    public synchronized ExecutorService getWorkExecutorService() {
        if (baseWorkExecutorService == null) {
            baseWorkExecutorService = new BaseWorkExecutorService();
        }
        return baseWorkExecutorService;
    }

    public synchronized void finish() {
        if (baseHttpExecutorService != null) {
            baseHttpExecutorService.shutdown();
            baseHttpExecutorService = null;
        }
        if (baseSingleWorkExecutorService != null) {
            baseSingleWorkExecutorService.shutdown();
            baseSingleWorkExecutorService = null;
        }
        if (baseWorkExecutorService != null) {
            baseWorkExecutorService.shutdown();
            baseWorkExecutorService = null;
        }
    }

}
