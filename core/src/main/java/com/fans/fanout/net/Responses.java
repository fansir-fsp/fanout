package com.fans.fanout.net;

/**
 * @author ：fsp
 * @date ：2020/5/15 16:28
 */
public interface Responses {

    Response successResponse(String ticketNO, Object result, Class resultType);

    Response systemFailResponse();

    Response systemFailResponse(String ticketNO);
}
