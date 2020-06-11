package com.fans.fanout.net;

import com.fans.fanout.serialization.Coder;

public interface Session {
    /**
     * 读取 request
     *
     * @return
     */
    Request readRequest(Coder coder);

    /**
     * 读取 response
     *
     * @return
     */
    Response readResponse(Coder coder);

    /**
     * 写回 response
     *
     * @param response
     */
    void writeResponse(Response response, Coder coder);

    /**
     * 写 request
     *
     * @param request
     * @param coder
     */
    void writeRequest(Request request, Coder coder);
}
