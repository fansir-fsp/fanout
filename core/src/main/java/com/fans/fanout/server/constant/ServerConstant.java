package com.fans.fanout.server.constant;

/**
 * 服务侧参数
 *
 * @author ：fsp
 * @date ：2020/4/16 17:43
 */
public class ServerConstant {

    public static final Integer REACTOR_THREAD = Runtime.getRuntime().availableProcessors() + 1;

    public static Integer CONSTANT_DEFAULT_START_PORT = 16000;

    public static String CODE_PROTOCOL_NAME = "org";

    public static Integer HANDLER_CORE_THREAD = Runtime.getRuntime().availableProcessors();

    public static Integer HANDLER_QUEUE_SIZE = 20000;
}
