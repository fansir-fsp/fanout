package com.fans.fanout.serialization.org;

import com.fans.fanout.net.Request;
import com.fans.fanout.net.Requests;

import java.util.Map;

/**
 * @author ：fsp
 * @date ：2020/6/5 11:57
 */
public class FanoutRequests implements Requests {
    @Override
    public Request buildRequests(String ticketNO, String methodName, Map<String, Object> parameterInstanceMap, String resultType) {
        return new FanoutRequest(ticketNO, methodName, parameterInstanceMap, resultType);
    }
}
