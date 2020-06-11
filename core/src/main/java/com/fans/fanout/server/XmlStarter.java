package com.fans.fanout.server;

import com.fans.fanout.server.config.ServerConfigManager;
import com.fans.fanout.skeleton.parser.XmlSkeletonParser;

import java.io.InputStream;

/**
 * xml方式启动器
 *
 * @author ：fsp
 * @date ：2020/4/17 14:06
 */
public class XmlStarter extends AbsStarter {

    private XmlSkeletonParser skeletonParser;

    public XmlStarter(InputStream inputStream) {
        skeletonParser = new XmlSkeletonParser(inputStream);
    }

    @Override
    protected void parseServerConfig() {
        this.server.setServerConfig(ServerConfigManager.getInstance().parse().getServerConfig());
    }

    @Override
    protected void loadSkeleton() throws Exception {
        this.server.registerSkeleton(skeletonParser.parseDoc().parseSkeleton());
    }

}
