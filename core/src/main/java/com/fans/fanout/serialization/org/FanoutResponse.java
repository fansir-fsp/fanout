package com.fans.fanout.serialization.org;

import com.alibaba.fastjson.JSON;
import com.fans.fanout.net.Response;
import com.fans.fanout.support.exception.ExpConsumer;
import com.fans.fanout.support.exception.ExpStandard;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 建议序列化response实现：fastJson方式
 *
 * @author ：fsp
 * @date ：2020/5/9 18:41
 */
public class FanoutResponse extends Response {

    public FanoutResponse() {
    }

    public FanoutResponse(int code, String msg, String ticketNO) {
        super(code, msg, ticketNO);
    }

    public FanoutResponse(int code, String msg, String ticketNO,
                          Map<String, Object> parameterInstanceMap, Object result, Class resultType) {
        super(code, msg, ticketNO, parameterInstanceMap, result, resultType);
    }

    @Override
    public byte[] encode() {
        FanoutAttr fanoutAttr = new FanoutAttr();
        fanoutAttr.code = this.code;
        fanoutAttr.msg = this.msg;

        Map<String, String> parameterTypeMap = new HashMap();
        Map<String, String> parameterInstanceMap = new HashMap();
        this.parameterInstanceMap.forEach((key, value) -> {
            parameterTypeMap.put(key, value.getClass().getName());
            parameterInstanceMap.put(key, JSON.toJSONString(value));
        });
        fanoutAttr.parameterTypeMap = parameterTypeMap;
        fanoutAttr.parameterInstanceMap = parameterInstanceMap;

        fanoutAttr.result = JSON.toJSONString(this.result);
        fanoutAttr.resultType = this.resultType.getName();
        fanoutAttr.ticket = this.ticketNO;
        return JSON.toJSONBytes(fanoutAttr);
    }

    @Override
    protected void decode() throws Exception {
        FanoutAttr fanoutAttr = JSON.parseObject(this.orgData, FanoutAttr.class);
        if (fanoutAttr == null
                || fanoutAttr.code == null
                || StringUtils.isEmpty(fanoutAttr.ticket)
                || StringUtils.isEmpty(fanoutAttr.resultType)) {
            throw new RuntimeException("解包错误，fanoutReq必传参数为空");
        }
        this.code = fanoutAttr.code;
        this.msg = fanoutAttr.msg;
        this.ticketNO = fanoutAttr.ticket;

        Map<String, String> parameterTypeMap =
                fanoutAttr.parameterTypeMap != null ? fanoutAttr.parameterTypeMap : new HashMap();
        Map<String, String> parameterInstanceOrgMap =
                fanoutAttr.parameterInstanceMap != null ? fanoutAttr.parameterInstanceMap : new HashMap();
        Map<String, Object> parameterInstanceMap = new HashMap();

        ExpConsumer<Map.Entry<String, String>> expConsumer = entry -> {
            String key = entry.getKey();
            String parameterType = entry.getValue();
            String parameterInstanceStr = parameterInstanceOrgMap.get(key);
            parameterInstanceMap.put(key, JSON.parseObject(parameterInstanceStr, Class.forName(parameterType)));
        };
        ExpStandard.wrapAndCheckIteratorConsumer(new ArrayList(parameterTypeMap.entrySet()), expConsumer);
        this.parameterInstanceMap = parameterInstanceMap;

        this.resultType = Class.forName(fanoutAttr.resultType);
        //非void解析返回值
        if (!Void.class.isAssignableFrom(this.resultType)) {
            this.result = JSON.parseObject(fanoutAttr.result, this.resultType);
        }
    }

    private static class FanoutAttr {
        private Integer code;

        private String msg;

        private String result;

        private String resultType;

        private String ticket;

        private Map<String, String> parameterInstanceMap;

        private Map<String, String> parameterTypeMap;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public String getTicket() {
            return ticket;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public Map<String, String> getParameterInstanceMap() {
            return parameterInstanceMap;
        }

        public void setParameterInstanceMap(Map<String, String> parameterInstanceMap) {
            this.parameterInstanceMap = parameterInstanceMap;
        }

        public Map<String, String> getParameterTypeMap() {
            return parameterTypeMap;
        }

        public void setParameterTypeMap(Map<String, String> parameterTypeMap) {
            this.parameterTypeMap = parameterTypeMap;
        }
    }
}
