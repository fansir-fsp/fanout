package com.fans.fanout.net.nio.accept;

import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.net.nio.TCPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;

/**
 * 处理写事件
 *
 * @author ：fsp
 * @date ：2020/5/12 10:24
 */
public class WritableAcceptor implements Acceptor {

    Logger logger = LoggerFactory.getLogger(WritableAcceptor.class);

    @Override
    public void dispatch(SelectionKey selectionKey, SelectorManager selectorManager) throws Exception {
        logger.info("处理write事件, selectionKey:{}", selectionKey);

        TCPSession tcpSession = (TCPSession) selectionKey.attachment();
        tcpSession.writeChannel();
    }
}
