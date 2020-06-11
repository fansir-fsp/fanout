package com.fans.fanout.server.config;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务端配置管理器
 *
 * @author ：fsp
 * @date ：2020/4/17 10:45
 */
public class ServerConfigManager {

    private static ServerConfigManager serverConfigManager = new ServerConfigManager();

    private ServerConfigManager() {
    }

    Lock parseLock = new ReentrantLock();

    private volatile ServerConfig serverConfig;

    public static ServerConfigManager getInstance() {
        return serverConfigManager;
    }

    public ServerConfigManager parse() {
        if (serverConfig == null) {
            parseLock.lock();
            try {
                if (serverConfig == null) {
                    serverConfig = new ServerConfig();
                }
            } finally {
                parseLock.unlock();
            }
        }
        return this;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
