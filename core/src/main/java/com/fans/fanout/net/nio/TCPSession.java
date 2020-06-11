package com.fans.fanout.net.nio;

import com.fans.fanout.net.Request;
import com.fans.fanout.net.Response;
import com.fans.fanout.net.Session;
import com.fans.fanout.serialization.Coder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TCP长连接实现主体
 *
 * @author ：fsp
 * @date ：2020/4/21 20:38
 */
public class TCPSession implements Session, Comparable {

    private Logger log = LoggerFactory.getLogger(TCPSession.class);

    private int bufferSize = 1024 * 2;

    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    private Reactor reactor;

    /**
     * 最后操作时间 - 判断长连接是否关闭
     */
    private long lastOperationTime = System.currentTimeMillis();

    /**
     * 处理tcp粘包/拆包问题，需要一个和解包业务侧连接的缓冲区
     * 该包装后的缓冲区写操作不保证线程安全（ps:读事件监听和数据搬移串行）
     */
    private ExpandedBuffer toBeParsedBuffer = null;

    private Queue<ByteBuffer> writeBufferQueue = new LinkedBlockingQueue<>();

    private volatile Status status = Status.NOT_CONNECTED;

    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    public enum Status {
        SERVER_CONNECTING, CLIENT_CONNECTING, NOT_CONNECTED
    }

    @Override
    public int compareTo(Object obj) {
        TCPSession next = (TCPSession) obj;
        if (this.lastOperationTime - next.lastOperationTime > 0) {
            return 1;
        }
        return 0;
    }

    private enum SessionTypeEnum {
        REQUEST, RESPONSE;
    }

    public TCPSession(SocketChannel socketChannel, SelectionKey selectionKey) {
        this.socketChannel = socketChannel;
        this.selectionKey = selectionKey;
    }

    public TCPSession(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Reactor getReactor() {
        return reactor;
    }

    public int readChannel() throws Exception {
        if (toBeParsedBuffer == null) {
            toBeParsedBuffer = new ExpandedBuffer(ByteBuffer.allocate(bufferSize * 2));
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);

        //status 1-还有数据 0-无数据 1-已关闭
        int channelStatus;
        try {
            while ((channelStatus = socketChannel.read(byteBuffer)) > 0) {
                byteBuffer.flip();
                toBeParsedBuffer.put(byteBuffer.array(),
                        byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
                byteBuffer.clear();
            }
            if (channelStatus < 0) {
                //关闭channel - 当前selector线程直接关
                log.info("客户端主动关闭");
                close();
            }
        } catch (IOException e) {
            channelStatus = -1;
            close();
            log.warn("channel读取数据时异常, error stack:", e);
        }
        return channelStatus;
    }

    //给selector线程调用
    public void close() throws IOException {
        if (status == Status.SERVER_CONNECTING) {
            TCPSessionManager.unRegister(this);
        }
        if (socketChannel != null) {
            socketChannel.close();
            selectionKey.cancel();
        }
        status = Status.NOT_CONNECTED;
    }

    //给selector以外线程调用
    public void softClose() throws IOException {
        reactor.unRegister(this);
        reactor.getSelector().wakeup();
    }

    /**
     * writeBufferQueue写入channel - 由reactor线程执行，串行 - synchronized用于保险
     *
     * @throws Exception
     */
    public synchronized void writeChannel() throws Exception {
        while (true) {
            ByteBuffer byteBuffer = writeBufferQueue.peek();
            if (byteBuffer == null) {
                //TODO:此处的注册事件会不会覆盖其它线程offer queue后的写事件注册动作？待验证
                selectionKey.interestOps(SelectionKey.OP_READ);
                reactor.getSelector().wakeup();
                break;
            }
            //socketChannel close的情况下，此处会抛异常
            int writeResult = socketChannel.write(byteBuffer);

            //TODO:打印写出channel信息
            /*log.info("写完测试 localAddress:{} remoteAddress:{} isConnected:{}",
                    socketChannel.getLocalAddress(), socketChannel.getRemoteAddress(), socketChannel.isConnected());
            socketChannel.close();*/

            if (writeResult == 0 && byteBuffer.remaining() > 0) {
                //缓冲区已满，保留该byteBuffer, 直到缓冲区有空间后再次触发
                selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                reactor.getSelector().wakeup();
                break;
            }
            writeBufferQueue.remove();
        }
    }

    //串行无多线程风险
    @Override
    public Request readRequest(Coder coder) {
        byte[] bytes = readReqOrRep();
        if (bytes == null) {
            //粘包或拆包导致buffer非完整包
            return null;
        }
        Request request = coder.decodeRequest(bytes);
        return request;
    }

    @Override
    public Response readResponse(Coder coder) {
        byte[] bytes = readReqOrRep();
        if (bytes == null) {
            //粘包或拆包导致buffer非完整包
            return null;
        }
        Response response = coder.decodeResponse(bytes);
        return response;
    }

    private byte[] readReqOrRep() {

        //toBeParsedBuffer 一直处于写模式，需要一个指针副本进行读取
        ByteBuffer tempBuffer = toBeParsedBuffer.duplicate();
        tempBuffer.flip();

        if (tempBuffer.remaining() < Integer.BYTES) {
            //长度标记位不足
            return null;
        }
        int size = tempBuffer.getInt();
        if (size <= 0) {
            //标记位长度不合法
            return null;
        }
        if (size > tempBuffer.remaining()) {
            //标记位长度超出
            return null;
        }

        byte[] bytes = new byte[size];
        tempBuffer.get(bytes);

        //读取成功后 toBeParsedBuffer将已读的部分截去(串行不考虑多线程问题)
        toBeParsedBuffer = new ExpandedBuffer(ByteBuffer.allocate(bufferSize * 2));
        toBeParsedBuffer.put(tempBuffer.array(),
                tempBuffer.position(), tempBuffer.limit() - tempBuffer.position());

        return bytes;
    }

    @Override
    public void writeResponse(Response response, Coder coder) {
        byte[] bytes = coder.encodeResponse(response);
        writeReqOrRep(bytes);
    }

    @Override
    public void writeRequest(Request request, Coder coder) {
        byte[] bytes = coder.encodeRequest(request);
        writeReqOrRep(bytes);
    }

    private void writeReqOrRep(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + Integer.BYTES);
        byteBuffer.clear();
        //放入头部计数位解决拆包粘包
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes, 0, bytes.length);
        byteBuffer.flip();
        writeBufferQueue.offer(byteBuffer);

        //如果处理时间超时，session已被清理，则抛CancelledKeyException
        //TODO:关注写事件-此处注册的写事件会不会被reactor线程刷新事件覆盖-待验证
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
        reactor.getSelector().wakeup();
    }

    public long getLastOperationTime() {
        return lastOperationTime;
    }

    public void updateLastOperationTime() {
        lastOperationTime = System.currentTimeMillis();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    /**
     * connection连接成功解锁
     */
    public void finishConnect() {
        connectionLatch.countDown();
    }

    /**
     * 等待connection连接成功解锁
     */
    public boolean waitForConnect(int timeout) {
        try {
            boolean connectFlag = connectionLatch.await(timeout, TimeUnit.MILLISECONDS);
            if (!connectFlag) {
                log.warn("等待连接超时");
            }
            return connectFlag;
        } catch (InterruptedException e) {
            log.warn("等待连接动作中断, error stack:", e);
        }
        return false;
    }
}
