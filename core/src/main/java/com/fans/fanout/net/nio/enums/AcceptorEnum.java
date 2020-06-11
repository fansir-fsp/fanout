package com.fans.fanout.net.nio.enums;

import com.fans.fanout.net.nio.accept.*;

import java.nio.channels.SelectionKey;

/**
 * @author ：fsp
 * @date ：2020/5/12 10:11
 */
public enum AcceptorEnum {

    CONNECTABLE(new ConnectableAcceptor()),
    ACCEPTABLE(new AcceptableAcceptor()),
    READABLE(new ReadableAcceptor()),
    WRITABLE(new WritableAcceptor());

    private Acceptor acceptor;

    AcceptorEnum(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public static Acceptor parseAcceptor(SelectionKey key) {
        if (key.isConnectable()) {
            return CONNECTABLE.acceptor;
        }
        if (key.isAcceptable()) {
            return ACCEPTABLE.acceptor;
        }
        if (key.isReadable()) {
            return READABLE.acceptor;
        }
        if (key.isWritable()) {
            return WRITABLE.acceptor;
        }
        return null;
    }
}
