package com.fans.fanout.example;

/**
 * @author ：fsp
 * @date ：2020/4/17 17:49
 * @version: $
 */
public class HelloRequest {

    private String name;

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
