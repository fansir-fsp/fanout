package com.fans.fanout.net.nio.accept;

import com.fans.fanout.handler.HandlerExecutor;
import com.fans.fanout.handler.RequestHandler;
import com.fans.fanout.handler.ResponseHandler;
import com.fans.fanout.net.Request;
import com.fans.fanout.net.Response;
import com.fans.fanout.net.nio.SelectorManager;
import com.fans.fanout.net.nio.TCPSession;
import com.fans.fanout.serialization.Coder;
import com.fans.fanout.skeleton.ServiceSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;

/**
 * 处理读事件
 *
 * @author ：fsp
 * @date ：2020/5/12 10:23
 */
public class ReadableAcceptor implements Acceptor {

    Logger logger = LoggerFactory.getLogger(ReadableAcceptor.class);

    @Override
    public void dispatch(SelectionKey selectionKey, SelectorManager selectorManager) throws Exception {
        logger.info("处理read事件, selectionKey:{}", selectionKey);

        TCPSession tcpSession = (TCPSession) selectionKey.attachment();
        //对端关闭事件从此处读取
        if (tcpSession.readChannel() >= 0) {
            switch (tcpSession.getStatus()) {
                case SERVER_CONNECTING: {
                    //解析request并处理
                    readRequestAndDoHandler(selectorManager, tcpSession);
                    break;
                }
                case CLIENT_CONNECTING: {
                    //解析response
                    readResponse(selectorManager, tcpSession);
                    break;
                }
            }
        }
    }

    private void readRequestAndDoHandler(SelectorManager selectorManager, TCPSession tcpSession) throws Exception {
        //加循环处理粘包
        while (true) {
            Coder coder = selectorManager.getMasterAdapter().getCoder();
            if (coder == null) {
                throw new RuntimeException("协议初始化失败");
            }
            Request request = tcpSession.readRequest(coder);
            //拆包或数据解析完成
            if (request == null) {
                break;
            }
            //串行监听读事件+解析流已完成操作，改线程池异步执行逻辑
            HandlerExecutor handlerExecutor = selectorManager.getMasterAdapter().getHandlerExecutor();
            handlerExecutor.executor(() -> {
                try {
                    ServiceSkeleton serviceSkeleton = handlerExecutor.getServiceSkeleton();
                    Response response = new RequestHandler(serviceSkeleton, request).doHandler(coder);
                    tcpSession.writeResponse(response, coder);
                } catch (Exception e) {
                    //此处报错，客户端调用会超时，因为没有回包
                    logger.error("reactor线程写回失败, error stack:", e);
                }
            });
        }
    }

    private void readResponse(SelectorManager selectorManager, TCPSession tcpSession) throws Exception {
        while (true) {
            Coder coder = selectorManager.getMasterInvokerContext().getCoder();
            if (coder == null) {
                throw new RuntimeException("协议初始化失败");
            }
            Response response = tcpSession.readResponse(coder);
            //拆包粘包 或 数据解析完成
            if (response == null) {
                break;
            }
            Executor executor = selectorManager.getMasterInvokerContext().getHandlerExecutor();
            executor.execute(() -> {
                try {
                    new ResponseHandler(response).doHandler();
                } catch (Exception e) {
                    logger.error("处理response回包时失败, error stack:", e);
                }
            });
        }
    }
}
