package com.fans.fanout.serialization.org;

import com.alibaba.fastjson.JSON;
import com.fans.fanout.net.Response;
import org.apache.commons.lang3.StringUtils;

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
        fanoutAttr.parameterInstanceMap = this.parameterInstanceMap;
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
        this.parameterInstanceMap = fanoutAttr.parameterInstanceMap;
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

        private Map<String, Object> parameterInstanceMap;

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

        public Map<String, Object> getParameterInstanceMap() {
            return parameterInstanceMap;
        }

        public void setParameterInstanceMap(Map<String, Object> parameterInstanceMap) {
            this.parameterInstanceMap = parameterInstanceMap;
        }
    }
}
