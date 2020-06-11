package com.fans.fanout.server.config;

import com.fans.fanout.server.constant.ServerConstant;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端配置dto
 *
 * @author ：fsp
 * @date ：2020/4/16 19:55
 */
public class ServerConfig {

    private String name;

    private Integer defaultStartPort = ServerConstant.CONSTANT_DEFAULT_START_PORT;

    private AtomicInteger currentPort = new AtomicInteger(defaultStartPort);

    //servant - adapter映射配置
    private Integer reactorThread = ServerConstant.REACTOR_THREAD;

    //通信协议配置
    private String codeProtocol = ServerConstant.CODE_PROTOCOL_NAME;

    //thread Pool配置
    private Integer handlerCoreThread = ServerConstant.HANDLER_CORE_THREAD;

    private Integer handlerQueueSize = ServerConstant.HANDLER_QUEUE_SIZE;

    public Integer getDefaultStartPort() {
        return defaultStartPort;
    }

    public void setDefaultStartPort(Integer defaultStartPort) {
        this.defaultStartPort = defaultStartPort;
        currentPort = new AtomicInteger(this.defaultStartPort);
    }

    public Integer getReactorThread() {
        return reactorThread;
    }

    public void setReactorThread(Integer reactorThread) {
        this.reactorThread = reactorThread;
    }

    public Integer getNextPort() {
        return currentPort.getAndIncrement();
    }

    public String getCodeProtocol() {
        return codeProtocol;
    }

    public void setCodeProtocol(String codeProtocol) {
        this.codeProtocol = codeProtocol;
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
}
