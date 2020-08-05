package io.andy.pigeon.rpc.core.util;

import java.util.concurrent.*;

/**
 * thread pool util
 */
public class ThreadPoolUtil {

    /**
     * make server thread pool
     *
     * @param serverType
     * @return
     */
    public static ThreadPoolExecutor makeServerThreadPool(final String serverType) {
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                60,
                300,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, serverType + "-serverHandlerPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        throw new RuntimeException(serverType + " Thread pool is EXHAUSTED!");
                    }
                });        // default maxThreads 300, minThreads 60

        return serverHandlerPool;
    }

}
