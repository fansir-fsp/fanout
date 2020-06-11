package com.fans.fanout.client.constant;

/**
 * @author ：fsp
 * @date ：2020/5/22 18:16
 */
public class ClientConstant {

    public static final Integer REACTOR_CORE_THREAD = 2;

    public static String CODE_PROTOCOL_NAME = "org";

    public static Integer HANDLER_CORE_THREAD = Runtime.getRuntime().availableProcessors();

    public static Integer HANDLER_QUEUE_SIZE = 1024;

    public static Integer INVOKE_TIMEOUT = 3000;

    public static Integer CONNECT_TIMEOUT = 3000;

    public static Integer INVOKER_CONNECTOR_NUM = 4;
}
