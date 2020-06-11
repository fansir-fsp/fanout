package com.fans.fanout.net.enums;

import com.fans.fanout.net.Adapter;
import com.fans.fanout.net.nio.NIOAdapter;

/**
 * @author ：fsp
 * @date ：2020/4/18 14:24
 */
public class NetEnums {

    public enum AdapterType {

        NIO_ADAPTER(0, "nio_adapter", NIOAdapter.class);
        private int code;
        private String name;
        private Class<? extends Adapter> adapterClass;

        AdapterType(int code, String name, Class<? extends Adapter> adapterClass) {
            this.code = code;
            this.name = name;
            this.adapterClass = adapterClass;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Adapter> getAdapterClass() {
            return adapterClass;
        }
    }

    public enum ResponseCode {

        SUCCESS(0, "执行成功"),
        SYSTEM_FAIL(1, "系统错误"),
        ;

        private int code;
        private String msg;

        ResponseCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
