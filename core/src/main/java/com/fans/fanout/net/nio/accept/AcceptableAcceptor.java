package com.fans.fanout.net.nio.accept;

import com.fans.fanout.net.nio.Reactor;
import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.net.nio.TCPSession;
import com.fans.fanout.net.nio.TCPSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 处理客户端连接事件
 *
 * @author ：fsp
 * @date ：2020/5/12 10:23
 */
public class AcceptableAcceptor implements Acceptor {
    Logger logger = LoggerFactory.getLogger(AcceptableAcceptor.class);

    @Override
    public void dispatch(SelectionKey selectionKey, SelectorManager selectorManager) throws Exception {
        logger.info("处理accept事件, selectionKey:{}", selectionKey);

        SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
        socketChannel.socket().setTcpNoDelay(true);
        socketChannel.configureBlocking(false);

        TCPSession tcpSession = new TCPSession(socketChannel, selectionKey);
        //acceptable事件build服务端session
        tcpSession.setStatus(TCPSession.Status.SERVER_CONNECTING);
        //注册session到管理中心
        TCPSessionManager.register(tcpSession);

        Reactor nextReactor = selectorManager.nextReactor();
        nextReactor.register(socketChannel, SelectionKey.OP_READ, tcpSession);
        nextReactor.getSelector().wakeup();
    }
}
