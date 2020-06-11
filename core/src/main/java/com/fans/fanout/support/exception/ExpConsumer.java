package com.fans.fanout.support.exception;

public interface ExpConsumer<T> {

    void accept(T t) throws Exception;

}