package com.fans.fanout.example.server.service;

import com.fans.fanout.example.HelloRequest;
import com.fans.fanout.example.HelloService;

/**
 * @author ：fsp
 * @date ：2020/4/17 17:28
 */
public class HelloServiceImpl implements HelloService {

    public Integer hello(HelloRequest rep, Integer age) {
        System.out.println("name:" + rep.getName());
        System.out.println("age:" + age);
        rep.setName(rep.getName() + age);
        return age + 80;
    }
}
