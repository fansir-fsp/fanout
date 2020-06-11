package com.fans.fanout.net.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TCP长连接管理器
 *
 * @author ：fsp
 * @date ：2020/5/12 15:13
 */
public class TCPSessionManager {

    private static Logger log = LoggerFactory.getLogger(TCPSessionManager.class);

    //默认session过期时间 - 1分钟
    private static int TIMEOUT_MILLISECOND = 1000 * 60 * 1;

    private static PriorityBlockingQueue<TCPSession> sessionQueue = new PriorityBlockingQueue();

    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    private TCPSessionManager() {
    }

    static {
        //每10秒执行一次过期清理
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                log.info("TCPSession 开始过期清理");
                long currentTimeMillis = System.currentTimeMillis();
                int clearCount = 0;
                while (true) {
                    TCPSession session = sessionQueue.peek();
                    if (session == null) {
                        break;
                    }
                    if (session.getLastOperationTime() + TIMEOUT_MILLISECOND <= currentTimeMillis) {
                        session.softClose();
                        clearCount++;
                    } else {
                        break;
                    }
                }
                log.info("TCPSession 清理完成，本次清理{}个", clearCount);
            } catch (Exception e) {
                log.error("TCPSession 过期清理失败，error stack:", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static void register(TCPSession session) {
        sessionQueue.offer(session);
    }

    public static void unRegister(TCPSession session) {
        sessionQueue.remove(session);
    }

}
