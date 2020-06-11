package com.fans.fanout.net.nio.config;

import com.fans.fanout.serialization.enums.ProtocolEnum;
import com.fans.fanout.server.config.ServerConfig;
import com.fans.fanout.server.config.ServerConfigManager;

/**
 * @author ：fsp
 * @date ：2020/4/18 16:01
 */
public class NIOAdapterConfig {

    private static String handlerGroupName = "server";

    private Integer port;

    private Integer reactorThread;

    private Integer handlerCoreThread;

    private Integer handlerQueueSize;

    private String handlerGroup;

    private ProtocolEnum protocol;


    public NIOAdapterConfig() {
        ServerConfig serverConfig = ServerConfigManager.getInstance().getServerConfig();
        this.reactorThread = serverConfig.getReactorThread();
        this.handlerCoreThread = serverConfig.getHandlerCoreThread();
        this.handlerQueueSize = serverConfig.getHandlerQueueSize();
        this.handlerGroup = handlerGroupName;
        this.protocol = ProtocolEnum.of(serverConfig.getCodeProtocol());
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getReactorThread() {
        return reactorThread;
    }

    public void setReactorThread(Integer reactorThread) {
        this.reactorThread = reactorThread;
    }

    public Integer getHandlerCoreThread() {
        return handlerCoreThread;
    }

    public void setHandlerCoreThread(Integer handlerCoreThread) {
        this.handlerCoreThread = handlerCoreThread;
    }

    public Integer getHandlerQueueSize() {
        return handlerQueueSize;
    }

    public void setHandlerQueueSize(Integer handlerQueueSize) {
        this.handlerQueueSize = handlerQueueSize;
    }

    public String getHandlerGroup() {
        return handlerGroup;
    }

    public void setHandlerGroup(String handlerGroup) {
        this.handlerGroup = handlerGroup;
    }

    public static String getHandlerGroupName() {
        return handlerGroupName;
    }

    public static void setHandlerGroupName(String handlerGroupName) {
        NIOAdapterConfig.handlerGroupName = handlerGroupName;
    }

    public ProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }
}
