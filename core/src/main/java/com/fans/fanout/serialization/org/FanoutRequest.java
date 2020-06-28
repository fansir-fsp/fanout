package com.fans.fanout.serialization.org;

import com.alibaba.fastjson.JSON;
import com.fans.fanout.net.Request;
import com.fans.fanout.support.exception.ExpConsumer;
import com.fans.fanout.support.exception.ExpStandard;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 建议序列化request实现：fastJson方式
 *
 * @author ：fsp
 * @date ：2020/5/9 18:40
 */
public class FanoutRequest extends Request {

    public FanoutRequest() {
    }

    public FanoutRequest(String ticketNO, String methodName, Map<String, Object> parameterInstanceMap, String resultType) {
        super(ticketNO, methodName, parameterInstanceMap, resultType);
    }

    @Override
    protected void decode() throws Exception {
        FanoutAttr fanoutAttr = JSON.parseObject(this.orgData, FanoutAttr.class);
        if (fanoutAttr == null
                || StringUtils.isEmpty(fanoutAttr.methodName)
                || StringUtils.isEmpty(fanoutAttr.ticketNO)
                || StringUtils.isEmpty(fanoutAttr.resultType)) {
            throw new RuntimeException("解包错误，fanoutReq必传参数为空");
        }

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

        this.methodName = fanoutAttr.methodName;
        this.ticketNO = fanoutAttr.ticketNO;
        this.resultType = fanoutAttr.resultType;
    }

    @Override
    public byte[] encode() {
        FanoutAttr fanoutAttr = new FanoutAttr();
        fanoutAttr.methodName = this.methodName;
        fanoutAttr.ticketNO = this.ticketNO;
        fanoutAttr.resultType = this.resultType;

        Map<String, String> parameterTypeMap = new HashMap();
        Map<String, String> parameterInstanceMap = new HashMap();
        this.parameterInstanceMap.forEach((key, value) -> {
            parameterTypeMap.put(key, value.getClass().getName());
            parameterInstanceMap.put(key, JSON.toJSONString(value));
        });
        fanoutAttr.parameterTypeMap = parameterTypeMap;
        fanoutAttr.parameterInstanceMap = parameterInstanceMap;
        return JSON.toJSONBytes(fanoutAttr);
    }

    private static class FanoutAttr {
        private String serviceName;

        private String methodName;

        private String ticketNO;

        private String resultType;

        private Map<String, String> parameterInstanceMap;

        private Map<String, String> parameterTypeMap;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getTicketNO() {
            return ticketNO;
        }

        public void setTicketNO(String ticketNO) {
            this.ticketNO = ticketNO;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
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
