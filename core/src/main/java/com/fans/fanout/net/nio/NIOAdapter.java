package com.fans.fanout.net.nio;

import com.fans.fanout.common.HandlerThreadPoolManager;
import com.fans.fanout.handler.HandlerExecutor;
import com.fans.fanout.net.Adapter;
import com.fans.fanout.net.nio.config.NIOAdapterConfig;
import com.fans.fanout.serialization.Coder;
import com.fans.fanout.server.config.ServerConfig;
import com.fans.fanout.server.config.ServerConfigManager;
import com.fans.fanout.skeleton.ServiceSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：fsp
 * @date ：2020/4/18 14:26
 */
public class NIOAdapter implements Adapter {

    Logger logger = LoggerFactory.getLogger(NIOAdapter.class);

    private NIOAdapterConfig adapterConfig;

    //selector管理器（每个selector对应一个reactor）
    private SelectorManager selectorManager;

    //handler业务执行线程池
    private HandlerExecutor handlerExecutor;

    //协议编解码器
    private Coder coder;

    public NIOAdapter() throws Exception {
        adapterConfig = new NIOAdapterConfig();
        selectorManager = new SelectorManager(this);
        coder = adapterConfig.getProtocol().getCoder();
    }

    public void bind(ServiceSkeleton skeleton) throws Exception {
        logger.info("adapter开始绑定skeleton, name{} ...", skeleton.getName());

        this.adapterConfig.setPort(skeleton.getPort());
        if (this.adapterConfig.getPort() == null) {
            ServerConfig serverConfig = ServerConfigManager.getInstance().getServerConfig();
            this.adapterConfig.setPort(serverConfig.getNextPort());
        }

        //初始化HandlerExecutor
        this.handlerExecutor =
                new HandlerExecutor(HandlerThreadPoolManager.getInstance().buildHandlerThreadPool(adapterConfig), skeleton);

        //初始化selector监听
        if (this.selectorManager == null) {
            throw new RuntimeException("selectorManager未初始化");
        }
        logger.info("adapter组件selectorManager开始启动, name:{}, port:{} ...", skeleton.getName(), skeleton.getPort());
        this.selectorManager.startup();
    }

    @Override
    public void stop() {
        selectorManager.stop();
    }

    public NIOAdapterConfig getAdapterConfig() {
        return adapterConfig;
    }

    public HandlerExecutor getHandlerExecutor() {
        return this.handlerExecutor;
    }

    public Coder getCoder() {
        return coder;
    }
}
