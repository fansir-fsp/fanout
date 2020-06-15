package com.fans.fanout.net.nio.client;

import com.fans.fanout.client.Client;
import com.fans.fanout.client.invoke.Connection;
import com.fans.fanout.client.invoke.InvokerContext;
import com.fans.fanout.net.nio.SelectorManager;

/**
 * @author ：fsp
 * @date ：2020/6/15 15:25
 */
public class NIOInvokerContext<T> extends InvokerContext<T> {

    /**
     * service维度分片
     */
    private volatile SelectorManager selectorManager;

    public NIOInvokerContext(Class<T> apiClass, Client client) throws Exception {
        super(apiClass, client);
        this.selectorManager = new SelectorManager(this);
        this.selectorManager.startup();
    }

    public SelectorManager getSelectorManager() {
        return this.selectorManager;
    }

    @Override
    public Connection buildConnection(String ip, Integer port) {
        return new NIOConnection(ip, port, this);
    }
}
