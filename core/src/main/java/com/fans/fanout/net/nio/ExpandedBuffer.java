package com.fans.fanout.net.nio;

import java.nio.ByteBuffer;

/**
 * 一个socketChannel只注册在一个reactor(selector)上
 * 对于一个缓存buffer的写操作是串行的，所以允许线程不安全
 *
 * @author ：fsp
 * @date ：2020/5/7 19:24
 * @version: $
 */
public class ExpandedBuffer {

    private ByteBuffer buffer;

    public ExpandedBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ExpandedBuffer flip() {
        this.buffer.flip();
        return this;
    }

    public ExpandedBuffer clear() {
        this.buffer.clear();
        return this;
    }

    public int getInt() {
        return this.buffer.getInt();
    }

    public int remaining() {
        return this.buffer.remaining();
    }

    public int position() {
        return this.buffer.position();
    }

    public ExpandedBuffer position(int position) {
        this.buffer.position(position);
        return this;
    }

    public ExpandedBuffer get(byte[] bytes) {
        this.buffer.get(bytes);
        return this;
    }

    public ExpandedBuffer put(byte[] src, int offset, int length) {
        autoExpand(length);
        this.buffer.put(src, offset, length);
        return this;
    }

    public ByteBuffer duplicate() {
        return this.buffer.duplicate();
    }

    //自动扩容
    private void autoExpand(int incLength) {
        int minCapacity = incLength + buffer.position();
        int expandedCapacity = buffer.capacity();
        while (minCapacity > expandedCapacity) {
            expandedCapacity *= 2;
        }

        ByteBuffer expandByteBuffer;
        if (buffer.isDirect()) {
            expandByteBuffer = ByteBuffer.allocateDirect(expandedCapacity);
        } else {
            expandByteBuffer = ByteBuffer.allocate(expandedCapacity);
        }
        expandByteBuffer.put(buffer.array());
        expandByteBuffer.position(buffer.position());
        buffer = expandByteBuffer;
    }
}
