package com.fans.fanout.server;

import com.fans.fanout.net.Adapter;
import com.fans.fanout.net.enums.NetEnums;
import com.fans.fanout.server.config.ServerConfig;
import com.fans.fanout.server.enums.ServerEnum;
import com.fans.fanout.skeleton.ServiceSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器
 *
 * @author ：fsp
 * @date ：2020/4/16 19:30
 */
public class Server {

    private Logger log = LoggerFactory.getLogger(Server.class);

    private AtomicInteger status = new AtomicInteger(ServerEnum.Status.STOP.getCode());

    private ServerConfig serverConfig;

    private ConcurrentHashMap<String, ServiceSkeleton> skeletonMap = new ConcurrentHashMap();

    private ConcurrentHashMap<String, Adapter> adapterMap = new ConcurrentHashMap<String, Adapter>();

    Class<? extends Adapter> adapterClass;

    private Starter starter;

    public Server(Starter starter) {
        this.starter = starter;
        adapterClass = NetEnums.AdapterType.NIO_ADAPTER.getAdapterClass();
    }

    public void start() {
        if (this.starter == null) {
            log.error("server starter 未设置");
            throw new RuntimeException("server starter 未设置");
        }
        starter.start(this);
    }

    public void stop() {
        try {
            setStatus(ServerEnum.Status.STOP);
            adapterMap.forEach((k, v) -> v.stop());
        } catch (Exception e) {
            log.error("服务关闭失败，error stack:", e);
        }
    }

    void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    void registerSkeleton(String skeletonName, ServiceSkeleton skeleton) {
        this.skeletonMap.putIfAbsent(skeletonName, skeleton);
    }

    void registerSkeleton(Map<String, ServiceSkeleton> skeletonMap) {
        this.skeletonMap.putAll(skeletonMap);
    }

    void bindSkeletons() throws Exception {
        if (skeletonMap != null && skeletonMap.size() > 0) {
            for (Map.Entry<String, ServiceSkeleton> skeletonEntry : skeletonMap.entrySet()) {
                try {
                    Adapter adapter = adapterClass.getConstructor().newInstance();
                    adapter.bind(skeletonEntry.getValue());
                    adapterMap.putIfAbsent(skeletonEntry.getKey(), adapter);
                } catch (Exception e) {
                    log.error("adapter绑定skeleton失败，error stack:", e);
                }
            }
        }
    }

    ServerEnum.Status getStatus() {
        return ServerEnum.Status.of(status.get());
    }

    boolean setStatus(ServerEnum.Status oldStatus, ServerEnum.Status ServerStatus) {
        return status.compareAndSet(oldStatus.getCode(), ServerStatus.getCode());
    }

    void setStatus(ServerEnum.Status serverStatus) {
        status.getAndSet(serverStatus.getCode());
    }
}
