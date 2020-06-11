package com.fans.fanout.common;

import com.fans.fanout.client.config.ClientConfig;
import com.fans.fanout.net.nio.config.NIOAdapterConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;

/**
 * handler执行线程池管理器
 *
 * @author ：fsp
 * @date ：2020/5/9 19:30
 */
public class HandlerThreadPoolManager {

    private static HandlerThreadPoolManager handlerThreadPoolManager = new HandlerThreadPoolManager();

    private static ConcurrentHashMap<String, Executor> adapterHandlerThreadPoolMap = new ConcurrentHashMap();

    private static ConcurrentHashMap<ClientConfig, Executor> clientConfigHandlerThreadPoolMap = new ConcurrentHashMap();

    private HandlerThreadPoolManager() {
    }

    public static HandlerThreadPoolManager getInstance() {
        return handlerThreadPoolManager;
    }

    public Executor buildHandlerThreadPool(NIOAdapterConfig adapterConfig) {
        if (adapterConfig == null || StringUtils.isEmpty(adapterConfig.getHandlerGroup())) {
            throw new RuntimeException("adapter配置类为空");
        }
        return buildHandlerThreadPool(adapterConfig.getHandlerGroup(), adapterHandlerThreadPoolMap,
                adapterConfig.getHandlerCoreThread(), adapterConfig.getHandlerQueueSize());
    }

    public Executor buildHandlerThreadPool(ClientConfig clientConfig) {
        if (clientConfig == null) {
            throw new RuntimeException("client配置类为空");
        }
        return buildHandlerThreadPool(clientConfig, clientConfigHandlerThreadPoolMap,
                clientConfig.getHandlerCoreThread(), clientConfig.getHandlerQueueSize());
    }

    private Executor buildHandlerThreadPool(Object key, ConcurrentHashMap executorMap, Integer coreSize, Integer queueSize) {
        Executor handlerThreadPool = (Executor) executorMap.get(key);
        if (handlerThreadPool == null) {
            handlerThreadPool = new ThreadPoolExecutor(coreSize, coreSize,
                    0L, TimeUnit.SECONDS, new ArrayBlockingQueue(queueSize));
            Executor oldHandlerThreadPool = (Executor) executorMap.putIfAbsent(key, handlerThreadPool);
            if (oldHandlerThreadPool != null) {
                handlerThreadPool = oldHandlerThreadPool;
            }
        }
        return handlerThreadPool;
    }
}
