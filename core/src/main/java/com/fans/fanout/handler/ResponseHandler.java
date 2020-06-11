package com.fans.fanout.handler;

import com.fans.fanout.client.ticket.Ticket;
import com.fans.fanout.client.ticket.TicketManager;
import com.fans.fanout.net.Response;

/**
 * @author ：fsp
 * @date ：2020/6/5 11:08
 */
public class ResponseHandler {

    private Response response;

    public ResponseHandler(Response response) {
        this.response = response;
    }

    public void doHandler() throws Exception {
        //ticket注入Response并解锁
        String ticketNO = response.getTicketNO();
        Ticket ticket = TicketManager.getInstance().getTicket(ticketNO);
        if (ticket != null) {
            ticket.setResponse(response);
        }
        ticket.notifyResponse();
    }
}
