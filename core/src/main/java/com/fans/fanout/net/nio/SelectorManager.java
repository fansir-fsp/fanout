package com.fans.fanout.net.nio;

import com.fans.fanout.client.invoke.InvokerContext;
import com.fans.fanout.net.nio.config.NIOAdapterConfig;
import com.fans.fanout.support.exception.ExpConsumer;
import com.fans.fanout.support.exception.ExpStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 组织reactor(selector)的单元
 * TODO:可以用继承区别客户端/服务端
 *
 * @author ：fsp
 * @date ：2020/4/18 18:53
 */
public class SelectorManager {

    Logger logger = LoggerFactory.getLogger(NIOAdapter.class);

    private NIOAdapter adapter;

    private InvokerContext invokerContext;

    private List<Reactor> reactorList = new ArrayList<>();

    private AtomicInteger reactorIndex = new AtomicInteger(0);

    private final Pattern pattern;

    enum Pattern {
        Server, Client
    }

    //服务端构建方法
    public SelectorManager(NIOAdapter adapter) throws Exception {
        this.adapter = adapter;

        int reactorNum = adapter.getAdapterConfig().getReactorThread();
        int index = 0;
        while (index < reactorNum) {
            Selector selector = Selector.open();
            Reactor reactor = new Reactor(selector, this);
            reactorList.add(reactor);
            index++;
        }
        this.pattern = Pattern.Server;
    }

    //客户端构建方法
    public SelectorManager(InvokerContext invokerContext) throws Exception {
        this.invokerContext = invokerContext;
        //构建reactor
        int reactorNum = invokerContext.getClientConfig().getReactorCoreThread();
        String skeletonName = invokerContext.getAnatomySkeleton().getApiClass().getName();
        int index = 0;
        while (index < reactorNum) {
            String reactorNo = skeletonName + "_" + index;
            Selector selector = Selector.open();
            Reactor reactor = new Reactor(reactorNo, selector, this);
            reactorList.add(reactor);
            index++;
        }
        this.pattern = Pattern.Client;
    }

    public void startup() throws Exception {
        logger.info("selectorManager组件reactorList开始启动selector监听, reactor size:{}", reactorList.size());

        //refresh reactorNo
        if (pattern == Pattern.Server) {
            for (int index = 0; index < reactorList.size(); index++) {
                String skeletonName = adapter.getHandlerExecutor().getServiceSkeleton().getName();
                String reactorNo = skeletonName + "_" + index;
                reactorList.get(index).setReactorNo(reactorNo);
            }
        }

        //reactor 线程start
        ExpConsumer<Reactor> expConsumer = reactor -> reactor.start();
        ExpStandard.wrapAndCheckIteratorConsumer(reactorList, expConsumer);
        if (pattern == Pattern.Server) {
            registerServerSocket();
        }
    }

    private void registerServerSocket() throws Exception {
        if (pattern == Pattern.Server) {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            NIOAdapterConfig adapterConfig = adapter.getAdapterConfig();
            if (adapterConfig == null || adapterConfig.getPort() == null) {
                throw new RuntimeException("端口号为空，无法绑定");
            }
            serverSocketChannel.bind(new InetSocketAddress(adapterConfig.getPort()));
            Reactor headReactor = nextReactor();
            headReactor.register(serverSocketChannel, SelectionKey.OP_ACCEPT, null);
            headReactor.getSelector().wakeup();
        }
    }

    public Reactor nextReactor() {
        int index = reactorIndex.incrementAndGet();
        return reactorList.get(index % reactorList.size());
    }

    public NIOAdapter getMasterAdapter() {
        return this.adapter;
    }

    public InvokerContext getMasterInvokerContext() {
        return this.invokerContext;
    }

    /**
     * 客户端注册socket 返回tcpSession给invoker持有作为发起网络传输的入口
     *
     * @param socketChannel
     * @return
     */
    public TCPSession registerSocket(SocketChannel socketChannel) {
        if (pattern == Pattern.Client) {
            Reactor headReactor = nextReactor();
            TCPSession tcpSession = new TCPSession(socketChannel);
            headReactor.register(socketChannel, SelectionKey.OP_CONNECT, tcpSession);
            headReactor.getSelector().wakeup();
            return tcpSession;
        }
        return null;
    }

    public void stop() {
        //打断selector监听动作
        reactorList.forEach(reactor -> reactor.interrupt());
    }

}
