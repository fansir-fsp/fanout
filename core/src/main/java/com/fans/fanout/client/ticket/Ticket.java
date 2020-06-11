package com.fans.fanout.client.ticket;

import com.fans.fanout.net.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author ：fsp
 * @date ：2020/6/4 19:13
 */
public class Ticket {

    private String ticketNo;

    private Response response;

    private CountDownLatch signal = new CountDownLatch(1);

    public boolean waitResponse(long timeout, TimeUnit unit) throws Exception {
        return signal.await(timeout, unit);
    }

    public void notifyResponse() {
        signal.countDown();
    }

    public Response getResponse() {
        return this.response;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
