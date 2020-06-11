package com.fans.fanout.net.nio.accept;

import com.fans.fanout.net.nio.SelectorManager;

import java.nio.channels.SelectionKey;

/**
 * @author ：fsp
 * @date ：2020/4/18 19:01
 */
public interface Acceptor {

    void dispatch(SelectionKey selectionKey, SelectorManager selectorManager) throws Exception;
}
