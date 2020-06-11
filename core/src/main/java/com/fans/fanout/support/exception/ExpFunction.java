package com.fans.fanout.support.exception;


/**
 * @author ：fsp
 * @date ：2020/4/20 21:15
 */
public interface ExpFunction<T, R> {

    R apply(T t) throws Exception;

}
