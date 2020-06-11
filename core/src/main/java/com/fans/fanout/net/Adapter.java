package com.fans.fanout.net;

import com.fans.fanout.skeleton.ServiceSkeleton;

public interface Adapter {

    /**
     * 绑定service skeleton 原型信息
     *
     * @param skeleton
     * @throws Exception
     */
    void bind(ServiceSkeleton skeleton) throws Exception;

    /**
     * adapter停止
     */
    void stop();
}
