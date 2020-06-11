package com.fans.fanout.server.enums;

/**
 * 服务端状态
 *
 * @author ：fsp
 * @date ：2020/4/16 19:41
 */
public class ServerEnum {

    public enum Status {
        FAIL(-1, "启动失败"),
        STOP(0, "停止"),
        STARTING(1, "启动中"),
        RUNNING(2, "运行中");

        private Integer code;
        private String des;

        private Status(Integer code, String des) {
            this.code = code;
            this.des = des;
        }

        public Integer getCode() {
            return code;
        }

        public String getDes() {
            return des;
        }

        public static Status of(int code) {
            for (Status status : Status.values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            return null;
        }
    }
}
