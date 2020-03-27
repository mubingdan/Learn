package com.example.router.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RouterPoolExecutor {

    private static ThreadPoolExecutor executor;

    // cpu数
    private static final int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    // 核心线程数
    private static final int MAX_CORE_POOL_SIZE = CPU_SIZE + 1;

    public static ThreadPoolExecutor newPoolExecutor(int coreSize) {
        if (coreSize <= 0) {
            return null;
        }

        coreSize = Math.min(coreSize, MAX_CORE_POOL_SIZE);
        executor = new ThreadPoolExecutor(coreSize, MAX_CORE_POOL_SIZE, 30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(64), new RouterThreadFactory());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static class RouterThreadFactory implements ThreadFactory {

        final AtomicInteger integer = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "router_thread_" + integer.getAndIncrement());
        }
    }
}
