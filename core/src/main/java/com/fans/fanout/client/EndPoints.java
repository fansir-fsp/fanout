package com.fans.fanout.client;

import java.util.List;

/**
 * 终端
 *
 * @author ：fsp
 * @date ：2020/5/25 15:26
 */
public class EndPoints {

    private String serviceName;

    private List<EndPoint> endPointList;

    public class EndPoint {

        private String ip;

        private Integer port;

        public EndPoint(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    public EndPoints() {

    }

    public EndPoints(String serviceName, List<EndPoint> endPointList) {
        this.serviceName = serviceName;
        this.endPointList = endPointList;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<EndPoint> getEndPointList() {
        return endPointList;
    }

    public void setEndPointList(List<EndPoint> endPointList) {
        this.endPointList = endPointList;
    }
}
