package com.fans.fanout.client;

import com.fans.fanout.server.Server;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：fsp
 * @date ：2020/5/25 15:42
 */
public class ServiceFactory {

    private Logger log = LoggerFactory.getLogger(Server.class);

    //保证每个service对应一个代理
    ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap();

    private ProxyFactory proxyFactory = new ProxyFactory();

    public <T> T createProxy(Class<T> apiClass, EndPoints endPoints, Client client) {
        try {
            String apiName = apiClass.getName();
            T proxy = (T) cacheMap.get(apiName);
            if (proxy != null) {
                return (T) proxy;
            }
            //createProxy 比较消耗资源，不用乐观自旋，直接上锁
            synchronized (this) {
                if (cacheMap.get(apiName) == null) {
                    proxy = proxyFactory.createProxy(apiClass, endPoints, client);
                    cacheMap.putIfAbsent(apiName, proxy);
                }
            }
            return (T) cacheMap.get(apiName);
        } catch (Exception e) {
            log.error("创建代理失败, error stack:", e);
        }
        return null;
    }
}
