package com.fans.fanout.client.invoke;

import com.fans.fanout.client.DynamicInvokeContext;
import com.fans.fanout.client.ticket.Ticket;
import com.fans.fanout.client.ticket.TicketManager;
import com.fans.fanout.net.Request;
import com.fans.fanout.net.Response;
import com.fans.fanout.net.enums.NetEnums;
import com.fans.fanout.serialization.Coder;
import com.fans.fanout.support.exception.ExpStandard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：fsp
 * @date ：2020/5/25 17:10
 */
public class Invoker<T> {

    private List<Connection> connections = new ArrayList();

    private volatile InvokerContext invokerContext;

    private AtomicInteger connectionCounter = new AtomicInteger(-1);

    public Invoker(String ip, Integer port, InvokerContext<T> invokerContext) {
        this.invokerContext = invokerContext;
        Integer invokerConnectorNum = invokerContext.getClientConfig().getInvokerConnectorNum();
        if (invokerConnectorNum == null) {
            throw new RuntimeException("invoker持有connector数未配置");
        }

        for (int i = 0; i < invokerConnectorNum; i++) {
            connections.add(invokerContext.buildConnection(ip, port));
        }
    }

    public InvokerContext getInvokerContext() {
        return invokerContext;
    }

    public void close() throws Exception {
        ExpStandard.wrapAndCheckIteratorConsumer(connections, connection -> connection.close());
    }

    public Object invoke(DynamicInvokeContext dynamicInvokeContext) throws Exception {
        if (connections == null || connections.size() == 0) {
            throw new RuntimeException("invoker没有connection建立");
        }

        int counter = connectionCounter.incrementAndGet();
        Connection connection = connections.get(counter % connections.size());

        //build ticket
        Ticket ticket = TicketManager.getInstance().registerTicket();

        //写request
        Coder coder = invokerContext.getCoder();
        if (coder == null) {
            throw new RuntimeException("协议初始化失败");
        }
        Request request = coder.getRequests().buildRequests(ticket.getTicketNo(), dynamicInvokeContext.getMethodName(),
                dynamicInvokeContext.getParameterInstanceMap(), dynamicInvokeContext.getResultTypeName());
        connection.write(request, coder);

        //ticket栅栏
        Integer invokeTimeout = invokerContext.getClientConfig().getInvokeTimeout();
        if (invokeTimeout == null) {
            throw new RuntimeException("invokeTimeout未配置");
        }
        if (!ticket.waitResponse(invokeTimeout, TimeUnit.MILLISECONDS)) {
            TicketManager.getInstance().clearTicket(request.getTicketNO());
            throw new RuntimeException("invoke timeout, request:" + request);
        }

        //从ticket中获取response
        Response response = ticket.getResponse();
        if (response == null) {
            throw new RuntimeException("invoke fail, response is null");
        }
        if (response.getCode() != NetEnums.ResponseCode.SUCCESS.getCode()) {
            throw new RuntimeException(
                    MessageFormat.format("invoke fail, response code is fail, code:{0}, msg:{1}",
                            response.getCode(), response.getMsg()));
        }
        return response.getResult();
    }
}
