package com.fans.fanout.net;

import java.util.Map;

/**
 * @author ：fsp
 * @date ：2020/5/9 18:36
 */
public abstract class Request {

    protected byte[] orgData;

    protected String methodName;

    protected String ticketNO;

    protected String resultType;

    protected Map<String, Object> parameterInstanceMap;

    private enum StatusEnum {
        UN, DONE, FAIL
    }

    //延迟解析包方法，把解包错误放到threadExecutor中，最大限度回包给客户端
    private volatile StatusEnum statusEnum = StatusEnum.UN;

    public Request() {
    }

    public Request(String ticketNO, String methodName, Map<String, Object> parameterInstanceMap, String resultType) {
        this.ticketNO = ticketNO;
        this.methodName = methodName;
        this.parameterInstanceMap = parameterInstanceMap;
        this.resultType = resultType;
    }


    public void setOrgData(byte[] orgData) {
        this.orgData = orgData;
    }

    public Map<String, Object> getParameterInstanceMap() throws Exception {
        initial();
        return parameterInstanceMap;
    }

    public String getMethodName() throws Exception {
        initial();
        return methodName;
    }

    public String getTicketNO() {
        return ticketNO;
    }

    public String getResultType() {
        return resultType;
    }

    private void initial() throws Exception {
        if (statusEnum == StatusEnum.UN) {
            synchronized (this) {
                if (statusEnum == StatusEnum.UN) {
                    try {
                        decode();
                    } catch (Exception e) {
                        statusEnum = StatusEnum.FAIL;
                        //TODO:此处可以细分回包时的错误类型
                        throw e;
                    }
                    statusEnum = StatusEnum.DONE;
                }
            }
        }
    }

    /**
     * 协议提取方法调用参数
     */
    protected abstract void decode() throws Exception;

    public abstract byte[] encode();
}
