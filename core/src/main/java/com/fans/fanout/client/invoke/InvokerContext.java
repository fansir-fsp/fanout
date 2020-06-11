package com.fans.fanout.client.invoke;

import com.fans.fanout.client.Client;
import com.fans.fanout.client.EndPoints;
import com.fans.fanout.client.ProxyFactory;
import com.fans.fanout.client.config.ClientConfig;
import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.serialization.Coder;
import com.fans.fanout.skeleton.AnatomySkeleton;
import com.fans.fanout.support.exception.ExpStandard;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author ：fsp
 * @date ：2020/5/25 18:43
 */
public class InvokerContext<T> {

    private static final long TIME_EXPIRED_CLARE_INTERVAL = 1000 * 30;
    private static final long TIME_EXPIRED_EXIST_BORDER = 1000 * 30;

    private Logger log = LoggerFactory.getLogger(ProxyFactory.class);

    private final Class<T> apiClass;

    private final AnatomySkeleton anatomySkeleton;

    private final Coder coder;

    private volatile List<Invoker> invokerList;

    private final Executor handlerExecutor;

    private final ClientConfig clientConfig;

    /**
     * service维度分片
     */
    private volatile SelectorManager selectorManager;

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2);

    private Queue<InvokersExpired> expiredQueue = new LinkedBlockingQueue();

    public InvokerContext(Class<T> apiClass, Client client) throws Exception {

        this.apiClass = apiClass;
        this.anatomySkeleton = new AnatomySkeleton(apiClass);
        this.coder = client.getClientConfig().getProtocolEnum().getCoder();
        this.clientConfig = client.getClientConfig();
        this.selectorManager = new SelectorManager(this);
        this.selectorManager.startup();
        this.handlerExecutor = client.getHandlerExecutor();
        initScheduleInvokerCleaner();
    }

    //防止异常原因导致的ScheduledThreadPoolExecutor过度膨胀
    private void initScheduleInvokerCleaner() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            while (true) {
                InvokersExpired invokersExpired = expiredQueue.peek();
                if (invokersExpired == null) {
                    break;
                }
                if (System.currentTimeMillis() - invokersExpired.getExpireTime() < TIME_EXPIRED_EXIST_BORDER) {
                    break;
                }
                doClearExpiredInvoker(invokersExpired);
                expiredQueue.poll();
            }
        }, TIME_EXPIRED_CLARE_INTERVAL, TIME_EXPIRED_CLARE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public ClientConfig getClientConfig() {
        return this.clientConfig;
    }

    public SelectorManager getSelectorManager() {
        return this.selectorManager;
    }

    public List<Invoker> getInvokerList() {
        return invokerList;
    }

    public void refresh(EndPoints endPoints) {
        List<EndPoints.EndPoint> endPointList = endPoints.getEndPointList();
        if (endPointList == null || endPointList.size() == 0) {
            throw new RuntimeException("终端服务列表为空");
        }
        List<Invoker> invokerList = new LinkedList();
        for (EndPoints.EndPoint endPoint : endPointList) {
            String ip = endPoint.getIp();
            Integer port = endPoint.getPort();
            invokerList.add(new Invoker(ip, port, this));
        }
        List<Invoker> oldInvokerList = this.invokerList;
        this.invokerList = invokerList;

        //延时关闭旧invoker
        expiredQueue.offer(new InvokersExpired(oldInvokerList));
    }

    /**
     * 手动清理过期invoker
     */
    public void clearExpiredInvoker() {
        while (true) {
            InvokersExpired invokersExpired = expiredQueue.peek();
            if (invokersExpired == null) {
                break;
            }
            doClearExpiredInvoker(invokersExpired);
            expiredQueue.poll();
        }
    }

    private void doClearExpiredInvoker(InvokersExpired invokersExpired) {
        List<Invoker> invokerList = invokersExpired.getInvokerList();
        if (invokerList != null && invokerList.size() > 0) {
            scheduledExecutorService.schedule(() -> {
                try {
                    ExpStandard.wrapAndCheckIteratorConsumer(invokerList, invoker -> invoker.close());
                } catch (Exception e) {
                    log.error("invoker关闭异常 error stack:", e);
                }
            }, clientConfig.getInvokeTimeout(), TimeUnit.MILLISECONDS);
        }
    }

    private static class InvokersExpired {
        private Long expireTime;
        private List<Invoker> invokerList;

        public InvokersExpired(List<Invoker> invokerList) {
            this.expireTime = System.currentTimeMillis();
            this.invokerList = invokerList;
        }

        public List<Invoker> getInvokerList() {
            return invokerList;
        }

        public Long getExpireTime() {
            return expireTime;
        }
    }

    public Coder getCoder() {
        return coder;
    }

    public Executor getHandlerExecutor() {
        return handlerExecutor;
    }

    public AnatomySkeleton getAnatomySkeleton() {
        return anatomySkeleton;
    }
}
