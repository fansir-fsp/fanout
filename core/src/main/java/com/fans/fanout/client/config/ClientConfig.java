package com.fans.fanout.client.config;

import com.fans.fanout.client.constant.ClientConstant;
import com.fans.fanout.serialization.enums.ProtocolEnum;

/**
 * @author ：fsp
 * @date ：2020/5/22 18:49
 */
public class ClientConfig {

    //selectorManager开reactor个数配置
    private Integer reactorCoreThread = ClientConstant.REACTOR_CORE_THREAD;

    //一个invoker连接开多少connector(channel)配置
    private Integer invokerConnectorNum = ClientConstant.INVOKER_CONNECTOR_NUM;

    //通信协议配置
    private ProtocolEnum protocolEnum = ProtocolEnum.of(ClientConstant.CODE_PROTOCOL_NAME);

    //thread Pool配置
    private static String handlerGroupName = "client";

    private Integer handlerCoreThread = ClientConstant.HANDLER_CORE_THREAD;

    private Integer handlerQueueSize = ClientConstant.HANDLER_QUEUE_SIZE;

    //调用超时配置
    private Integer invokeTimeout = ClientConstant.INVOKE_TIMEOUT;

    private Integer connectTimeout = ClientConstant.CONNECT_TIMEOUT;

    public static String getHandlerGroupName() {
        return handlerGroupName;
    }

    public static void setHandlerGroupName(String handlerGroupName) {
        ClientConfig.handlerGroupName = handlerGroupName;
    }

    public Integer getReactorCoreThread() {
        return reactorCoreThread;
    }

    public void setReactorCoreThread(Integer reactorCoreThread) {
        this.reactorCoreThread = reactorCoreThread;
    }

    public Integer getInvokerConnectorNum() {
        return invokerConnectorNum;
    }

    public void setInvokerConnectorNum(Integer invokerConnectorNum) {
        this.invokerConnectorNum = invokerConnectorNum;
    }

    public ProtocolEnum getProtocolEnum() {
        return protocolEnum;
    }

    public void setProtocolEnum(ProtocolEnum protocolEnum) {
        this.protocolEnum = protocolEnum;
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

    public Integer getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(Integer invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }


    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
