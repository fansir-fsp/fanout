package com.fans.fanout.serialization.org;

import com.fans.fanout.net.Response;
import com.fans.fanout.net.Responses;
import com.fans.fanout.net.enums.NetEnums;

import java.util.Map;

/**
 * 简易协议response生成器
 *
 * @author ：fsp
 * @date ：2020/5/20 19:43
 */
public class FanoutResponses implements Responses {

    @Override
    public Response successResponse(String ticketNO, Map<String, Object> parameterInstanceMap, Object result, Class resultType) {
        return new FanoutResponse(NetEnums.ResponseCode.SUCCESS.getCode(),
                NetEnums.ResponseCode.SUCCESS.getMsg(), ticketNO, parameterInstanceMap, result, resultType);
    }

    @Override
    public Response systemFailResponse() {
        return new FanoutResponse(NetEnums.ResponseCode.SYSTEM_FAIL.getCode(),
                NetEnums.ResponseCode.SYSTEM_FAIL.getMsg(), null);
    }

    @Override
    public Response systemFailResponse(String ticketNO) {
        return new FanoutResponse(NetEnums.ResponseCode.SYSTEM_FAIL.getCode(),
                NetEnums.ResponseCode.SYSTEM_FAIL.getMsg(), ticketNO);
    }
}
