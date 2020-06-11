package com.fans.fanout.client;

import com.fans.fanout.client.invoke.InvokerContext;
import com.fans.fanout.client.loadBalance.LoadBalance;
import com.fans.fanout.client.loadBalance.PollingLoadBalance;

import java.lang.reflect.Proxy;

/**
 * @author ：fsp
 * @date ：2020/5/25 15:55
 */
public class ProxyFactory {

    public <T> T createProxy(Class<T> apiClass, EndPoints endPoints, Client client) throws Exception {
        //1.endPoints刷新
        if (endPoints == null) {
            //TODO: 刷新endPoints (服务注册机制 通过eureka或其它)
        }
        if (endPoints == null) {
            throw new RuntimeException("终端列表为空");
        }
        //2.endPoints build invokerList
        InvokerContext invokerContext = new InvokerContext(apiClass, client);
        invokerContext.refresh(endPoints);

        //3.loadBalance reset
        LoadBalance loadBalance = new PollingLoadBalance();
        loadBalance.reset(invokerContext.getInvokerList());
        invokerContext.clearExpiredInvoker();

        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{apiClass}, new ProxyInstance<T>(apiClass, invokerContext, loadBalance));
    }

}
