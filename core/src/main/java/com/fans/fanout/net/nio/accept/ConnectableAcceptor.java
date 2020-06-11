package com.fans.fanout.net.nio.accept;

import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.net.nio.TCPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author ：fsp
 * @date ：2020/5/26 19:23
 */
public class ConnectableAcceptor implements Acceptor {
    Logger logger = LoggerFactory.getLogger(ConnectableAcceptor.class);

    @Override
    public void dispatch(SelectionKey selectionKey, SelectorManager selectorManager) throws Exception {
        logger.info("处理connection事件, selectionKey:{}", selectionKey);

        SocketChannel channel = (SocketChannel) selectionKey.channel();
        TCPSession tcpSession = (TCPSession) selectionKey.attachment();
        if (tcpSession == null) {
            throw new RuntimeException("selectKey附件tcpSession未注册");
        }

        boolean connectFlag = channel.finishConnect();
        selectionKey.interestOps(SelectionKey.OP_READ);
        tcpSession.getReactor().getSelector().wakeup();
        tcpSession.setStatus(TCPSession.Status.CLIENT_CONNECTING);
        if (connectFlag) {
            tcpSession.finishConnect();
        }
    }
}
