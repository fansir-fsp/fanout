package com.fans.fanout.serialization.enums;

import com.fans.fanout.serialization.Coder;
import com.fans.fanout.serialization.org.FanoutCoder;

/**
 * @author ：fsp
 * @date ：2020/5/9 18:52
 */
public enum ProtocolEnum {

    ORG("org", "原生", new FanoutCoder());

    private String code;

    private String name;

    private Coder coder;

    ProtocolEnum(String code, String name, Coder coder) {
        this.code = code;
        this.name = name;
        this.coder = coder;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Coder getCoder() {
        return coder;
    }

    public static ProtocolEnum of(String code) {
        for (ProtocolEnum protocol : ProtocolEnum.values()) {
            if (protocol.getCode().equals(code)) {
                return protocol;
            }
        }
        return null;
    }
}
