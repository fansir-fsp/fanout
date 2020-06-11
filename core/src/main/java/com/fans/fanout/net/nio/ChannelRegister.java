package com.fans.fanout.net.nio;

import java.nio.channels.SelectableChannel;

/**
 * @author ：fsp
 * @date ：2020/4/21 20:35
 */
public class ChannelRegister {

    private SelectableChannel channel;

    private Integer op;

    private TCPSession tcpSession;

    public ChannelRegister(SelectableChannel channel, Integer op, TCPSession tcpSession) {
        this.channel = channel;
        this.op = op;
        this.tcpSession = tcpSession;
    }

    public SelectableChannel getChannel() {
        return channel;
    }

    public void setChannel(SelectableChannel channel) {
        this.channel = channel;
    }

    public Integer getOp() {
        return op;
    }

    public void setOp(Integer op) {
        this.op = op;
    }

    public TCPSession getTcpSession() {
        return tcpSession;
    }

    public void setTcpSession(TCPSession tcpSession) {
        this.tcpSession = tcpSession;
    }
}
