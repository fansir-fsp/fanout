package com.fans.fanout.handler;

import com.fans.fanout.skeleton.ServiceSkeleton;

import java.util.concurrent.Executor;

/**
 * @author ：fsp
 * @date ：2020/5/11 18:05
 */
public class HandlerExecutor {

    private Executor handlerTaskThreadPool;

    private ServiceSkeleton serviceSkeleton;

    public HandlerExecutor(Executor handlerTaskThreadPool, ServiceSkeleton serviceSkeleton) {
        this.handlerTaskThreadPool = handlerTaskThreadPool;
        this.serviceSkeleton = serviceSkeleton;
    }

    public ServiceSkeleton getServiceSkeleton() {
        return serviceSkeleton;
    }

    public void executor(Runnable runnable) {
        handlerTaskThreadPool.execute(runnable);
    }
}
