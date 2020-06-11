package com.fans.fanout.client.ticket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ：fsp
 * @date ：2020/6/4 19:18
 */
public class TicketManager {

    private AtomicLong ticketNoProducer = new AtomicLong(1);

    private ConcurrentHashMap<String, Ticket> ticketMap = new ConcurrentHashMap();

    private static TicketManager ticketManager = new TicketManager();

    private TicketManager() {
    }

    public static TicketManager getInstance() {
        return ticketManager;
    }

    public Ticket getTicket(String ticketNo) {
        return ticketMap.get(ticketNo);
    }

    public Ticket registerTicket() {
        Ticket ticket = new Ticket();
        long ticketNo;
        while (true) {
            ticketNo = ticketNoProducer.getAndAdd(1);
            if (ticketMap.putIfAbsent(Long.toString(ticketNo), ticket) == null) {
                break;
            }
        }
        ticket.setTicketNo(Long.toString(ticketNo));
        return ticket;
    }

    public void clearTicket(String ticketNo) {
        ticketMap.remove(ticketNo);
    }

}
