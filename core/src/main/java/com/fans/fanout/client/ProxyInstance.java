package com.fans.fanout.client;

import com.fans.fanout.client.invoke.Invoker;
import com.fans.fanout.client.invoke.InvokerContext;
import com.fans.fanout.client.loadBalance.LoadBalance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author ：fsp
 * @date ：2020/5/25 15:42
 */
public class ProxyInstance<T> implements InvocationHandler {

    private Class<T> apiClass;

    private InvokerContext invokerContext;

    private LoadBalance loadBalance;

    public ProxyInstance(Class<T> apiClass, InvokerContext invokerContext, LoadBalance loadBalance) {
        this.apiClass = apiClass;
        this.invokerContext = invokerContext;
        this.loadBalance = loadBalance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();

        //排除原生方法
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return this.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return this.equals(args[0]);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return this.toString();
        }

        DynamicInvokeContext dynamicInvokeContext = new DynamicInvokeContext(method, args);
        Invoker invoker = loadBalance.select();
        return invoker.invoke(dynamicInvokeContext);
    }
}
