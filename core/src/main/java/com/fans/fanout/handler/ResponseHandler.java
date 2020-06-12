package com.fans.fanout.handler;

import com.fans.fanout.client.ticket.Ticket;
import com.fans.fanout.client.ticket.TicketManager;
import com.fans.fanout.net.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ：fsp
 * @date ：2020/6/5 11:08
 */
public class ResponseHandler {

    Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private Response response;

    public ResponseHandler(Response response) {
        this.response = response;
    }

    public void doHandler() throws Exception {
        //ticket注入Response并解锁
        String ticketNO = response.getTicketNO();
        Ticket ticket = TicketManager.getInstance().getTicket(ticketNO);
        if (ticket == null) {
            logger.warn("ticketNo:{} 客户端已清理或未注册，回包无法正确处理", ticketNO);
            return;
        }
        ticket.setResponse(response);
        ticket.notifyResponse();
    }
}
