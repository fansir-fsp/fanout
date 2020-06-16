package com.fans.fanout.net;

import java.util.Map;

/**
 * @author ：fsp
 * @date ：2020/5/15 16:28
 */
public interface Responses {

    Response successResponse(String ticketNO, Map<String, Object> parameterInstanceMap, Object result, Class resultType);

    Response systemFailResponse();

    Response systemFailResponse(String ticketNO);
}
