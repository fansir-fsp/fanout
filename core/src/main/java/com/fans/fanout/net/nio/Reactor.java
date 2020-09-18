package com.fans.fanout.net.nio;

import com.fans.fanout.net.nio.enums.AcceptorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：fsp
 * @date ：2020/4/18 19:00
 */
public class Reactor extends Thread {

    private String reactorNo;

    private Logger log = LoggerFactory.getLogger(Reactor.class);

    private Selector selector;

    private SelectorManager selectorManager;

    private Queue<ChannelRegister> registerQueue = new LinkedBlockingQueue();

    private Queue<TCPSession> unRegisterQueue = new LinkedBlockingQueue();

    private boolean crashed = false;

    public Reactor(Selector selector, SelectorManager selectorManager) {
        this.selector = selector;
        this.selectorManager = selectorManager;
    }

    public Reactor(String reactorNo, Selector selector, SelectorManager selectorManager) {
        this.reactorNo = reactorNo;
        this.selector = selector;
        this.selectorManager = selectorManager;
    }

    public Selector getSelector() {
        return this.selector;
    }

    public void register(SelectableChannel channel, Integer op, TCPSession tcpSession) {
        registerQueue.offer(new ChannelRegister(channel, op, tcpSession));
    }

    public void unRegister(TCPSession tcpSession) {
        unRegisterQueue.offer(tcpSession);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                doRegister();
                dispatchEvent();
                doUnRegister();
            }
        } catch (Exception e) {
            crashed = true;
            log.error("reactor线程crash, reactorNo:{}", reactorNo);
            log.error("error stack:", e);
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                //此处报错，客户端调用会超时，因为没有回包
                log.error("reactor线程selector关闭失败, reactorNo:{}", reactorNo);
                log.error("error stack:", e);
            }
        }
    }

    private void doRegister() throws Exception {
        ChannelRegister channelRegister = registerQueue.poll();
        if (channelRegister != null) {
            SelectableChannel channel = channelRegister.getChannel();
            Integer op = channelRegister.getOp();
            TCPSession tcpSession = channelRegister.getTcpSession();

            log.info("reactorNo:{} 注册channel到selector, channel:{} op:{} session:{}",
                    reactorNo, channel, op, tcpSession);

            if (tcpSession != null) {
                //核心：channel 注册 selector
                SelectionKey key = channel.register(selector, op, tcpSession);
                tcpSession.setSelectionKey(key);
                tcpSession.setReactor(this);
            } else {
                channel.register(selector, op);
            }
        }
    }

    private void dispatchEvent() throws Exception {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            //selectionKey需要手动移除
            iterator.remove();

            //更新最后操作时间
            if (selectionKey.attachment() instanceof TCPSession) {
                ((TCPSession) selectionKey.attachment()).updateLastOperationTime();
            }

            AcceptorEnum.parseAcceptor(selectionKey).dispatch(selectionKey, selectorManager);
        }
    }

    private void doUnRegister() throws Exception {
        TCPSession tcpSession = unRegisterQueue.poll();
        if (tcpSession != null) {
            tcpSession.close();
        }
    }

    public void setReactorNo(String reactorNo) {
        this.reactorNo = reactorNo;
    }
}
