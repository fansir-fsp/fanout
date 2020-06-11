package com.fans.fanout.net;

import java.util.Map;

public interface Requests {

    Request buildRequests(String ticketNO, String methodName, Map<String, Object> parameterInstanceMap, String resultType);

}
