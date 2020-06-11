package com.fans.fanout.client.config;

/**
 * @author ：fsp
 * @date ：2020/5/22 18:49
 */
public class ServiceConfig {

    private String servantName;

    private Class apiClass;

    private ClientConfig clientConfig;

    public ServiceConfig(String servantName, Class apiClass, ClientConfig clientConfig) {
        this.servantName = servantName;
        this.apiClass = apiClass;
        this.clientConfig = clientConfig;
    }

    public String getServantName() {
        return servantName;
    }

    public void setServantName(String servantName) {
        this.servantName = servantName;
    }

    public Class getApiClass() {
        return apiClass;
    }

    public void setApiClass(Class apiClass) {
        this.apiClass = apiClass;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
