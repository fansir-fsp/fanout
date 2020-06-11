package com.fans.fanout.server;

import com.fans.fanout.server.enums.ServerEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动器
 *
 * @author ：fsp
 * @date ：2020/4/17 15:47
 */
public abstract class AbsStarter implements Starter {

    private Logger log = LoggerFactory.getLogger(AbsStarter.class);

    protected Server server;

    public void start(Server server) {
        this.server = server;

        //启动server flag
        boolean statusUpdate = false;
        while (!statusUpdate) {
            ServerEnum.Status oldStatusEnum = this.server.getStatus();
            if (oldStatusEnum != null && oldStatusEnum.getCode() <= ServerEnum.Status.STOP.getCode()) {
                statusUpdate = this.server.setStatus(oldStatusEnum, ServerEnum.Status.STARTING);
            } else {
                statusUpdate = false;
                break;
            }
        }

        try {
            if (!statusUpdate) {
                log.error("server 启动状态错误, status:{}", statusUpdate);
            }
            long startTime = System.currentTimeMillis();
            log.info("server 开始启动...");

            //1.解析系统配置
            parseServerConfig();
            //2.加载服务skeleton
            loadSkeleton();
            //3.服务skeleton 绑定服务端口
            this.server.bindSkeletons();
            this.server.setStatus(ServerEnum.Status.RUNNING);

            long endTime = System.currentTimeMillis();
            log.info("server 启动完成， 耗时:{} ms", endTime - startTime);
        } catch (Exception e) {
            log.error("服务启动失败，error stack:", e);
            this.server.setStatus(ServerEnum.Status.FAIL);
        }
    }

    protected abstract void parseServerConfig() throws Exception;

    protected abstract void loadSkeleton() throws Exception;

}
