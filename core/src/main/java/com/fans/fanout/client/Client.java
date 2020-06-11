package com.fans.fanout.client;

import com.fans.fanout.client.config.ClientConfig;
import com.fans.fanout.common.HandlerThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * @author ：fsp
 * @date ：2020/5/22 19:44
 */
public class Client {

    private Logger log = LoggerFactory.getLogger(Client.class);

    //client的handler线程池按照clientConfig分片
    private Executor handlerExecutor;

    private ClientConfig clientConfig;

    private ServiceFactory serviceFactory = new ServiceFactory();

    public Client(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        handlerExecutor = HandlerThreadPoolManager.getInstance().buildHandlerThreadPool(clientConfig);
    }

    public <T> T getProxy(Class<T> apiClass) {
        return serviceFactory.createProxy(apiClass, null, this);
    }

    public <T> T getProxy(Class<T> apiClass, EndPoints endPoints) {
        return serviceFactory.createProxy(apiClass, endPoints, this);
    }

    public Executor getHandlerExecutor() {
        return handlerExecutor;
    }

    public void setHandlerExecutor(Executor handlerExecutor) {
        this.handlerExecutor = handlerExecutor;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
