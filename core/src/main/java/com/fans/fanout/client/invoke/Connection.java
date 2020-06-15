package com.fans.fanout.client.invoke;

import com.fans.fanout.net.Request;
import com.fans.fanout.serialization.Coder;

import java.io.IOException;

/**
 * @author ：fsp
 * @date ：2020/5/25 17:14
 */
public interface Connection {

    /**
     * 保持连接状态
     *
     * @throws Exception
     */
    void ensureConnect() throws Exception;

    /**
     * 关闭连接
     *
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * 写入
     *
     * @param request
     * @param coder
     * @throws Exception
     */
    void write(Request request, Coder coder) throws Exception;
}
