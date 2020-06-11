package com.fans.fanout.client.loadBalance;

import com.fans.fanout.client.invoke.Invoker;

import java.util.List;

public interface LoadBalance {

    void reset(List<Invoker> invokerList);

    Invoker select();
}
