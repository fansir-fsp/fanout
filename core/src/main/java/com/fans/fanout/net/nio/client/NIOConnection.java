package com.fans.fanout.net.nio.client;

import com.fans.fanout.client.invoke.Connection;
import com.fans.fanout.net.Request;
import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.net.nio.TCPSession;
import com.fans.fanout.serialization.Coder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author ：fsp
 * @date ：2020/6/15 15:38
 */
public class NIOConnection implements Connection {

    Logger logger = LoggerFactory.getLogger(Connection.class);

    private final InetSocketAddress inetSocketAddress;
    private final SelectorManager selectorManager;
    private volatile Integer invokeTimeout;
    private volatile Integer connectTimeout;
    private volatile TCPSession tcpSession;

    public NIOConnection(String ip, Integer port, NIOInvokerContext invokerContext) {
        if (StringUtils.isEmpty(ip) || port == null) {
            throw new RuntimeException("终端ip|port为空");
        }
        inetSocketAddress = new InetSocketAddress(ip, port);
        this.selectorManager = invokerContext.getSelectorManager();
        this.invokeTimeout = invokerContext.getClientConfig().getInvokeTimeout();
        this.connectTimeout = invokerContext.getClientConfig().getConnectTimeout();
    }

    public void ensureConnect() throws Exception {
        //connection ensure操作加锁
        if (!isClientConnected()) {
            synchronized (this) {
                if (!isClientConnected()) {
                    SocketChannel socketChannel = SocketChannel.open();
                    //connection异步
                    socketChannel.configureBlocking(false);
                    socketChannel.connect(inetSocketAddress);
                    this.tcpSession = selectorManager.registerSocket(socketChannel);
                    //等待connection调用同步
                    if (!tcpSession.waitForConnect(connectTimeout)) {
                        throw new RuntimeException("连接失败");
                    }
                    logger.info("目标 address:{}, port:{} 连接完成", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                }
            }
        }
    }

    private boolean isClientConnected() {
        boolean clientConnected = this.tcpSession != null && tcpSession.getStatus() == TCPSession.Status.CLIENT_CONNECTING;
        return clientConnected;
    }

    public void close() throws IOException {
        tcpSession.softClose();
    }

    public void write(Request request, Coder coder) throws Exception {
        ensureConnect();
        tcpSession.writeRequest(request, coder);
    }
}
