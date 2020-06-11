package com.fans.fanout.client.loadBalance;

import com.fans.fanout.client.invoke.Invoker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单轮询负载选择器
 *
 * @author ：fsp
 * @date ：2020/5/27 19:29
 */
public class PollingLoadBalance implements LoadBalance {
    private volatile List<Invoker> invokerList;

    private AtomicInteger counter = new AtomicInteger(-1);

    @Override
    public void reset(List<Invoker> invokerList) {
        this.invokerList = invokerList;
    }

    @Override
    public Invoker select() {
        if (invokerList == null || invokerList.size() == 0) {
            return null;
        }
        int index = counter.getAndIncrement();
        return invokerList.get(index % invokerList.size());
    }
}
