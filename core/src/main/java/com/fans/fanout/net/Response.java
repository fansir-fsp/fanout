package com.fans.fanout.net;


/**
 * @author ：fsp
 * @date ：2020/5/9 18:38
 */
public abstract class Response {

    protected byte[] orgData;

    protected int code;

    protected String msg;

    protected String ticketNO;

    protected Object result;

    protected Class resultType;

    private enum Status {
        UN, DONE, FAIL
    }

    private volatile Response.Status statusEnum = Response.Status.UN;

    public Response() {
    }

    public Response(int code, String msg, String ticketNO) {
        this.code = code;
        this.msg = msg;
        this.ticketNO = ticketNO;
    }

    public Response(int code, String msg, String ticketNO, Object result, Class resultType) {
        this.code = code;
        this.msg = msg;
        this.ticketNO = ticketNO;
        this.result = result;
        this.resultType = resultType;
    }

    public byte[] getOrgData() {
        return orgData;
    }

    public void setOrgData(byte[] orgData) {
        this.orgData = orgData;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getTicketNO() throws Exception {
        initial();
        return ticketNO;
    }

    public Object getResult() {
        return result;
    }

    private void initial() throws Exception {
        if (statusEnum == Status.UN) {
            synchronized (this) {
                if (statusEnum == Status.UN) {
                    try {
                        decode();
                    } catch (Exception e) {
                        statusEnum = Status.FAIL;
                        //TODO:此处可以细分回包时的错误类型
                        throw e;
                    }
                    statusEnum = Status.DONE;
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
