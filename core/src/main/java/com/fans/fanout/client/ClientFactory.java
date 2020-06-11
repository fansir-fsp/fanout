package com.fans.fanout.client;

import com.fans.fanout.client.config.ClientConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：fsp
 * @date ：2020/5/25 12:04
 */
public class ClientFactory {

    ConcurrentHashMap<ClientConfig, Client> clientMap = new ConcurrentHashMap();

    private static ClientFactory clientFactory = new ClientFactory();

    private ClientFactory() {

    }

    public ClientFactory getInstance() {
        return clientFactory;
    }

    public Client getClient(ClientConfig clientConfig) {

        Client existClient = clientMap.get(clientConfig);
        if (existClient != null) {
            return existClient;
        }
        //考虑到build client占用资源，不用乐观自旋，直接上锁
        synchronized (this) {
            if (clientMap.get(clientConfig) == null) {
                clientMap.putIfAbsent(clientConfig, new Client(clientConfig));
            }
        }
        return clientMap.get(clientConfig);
    }

}
