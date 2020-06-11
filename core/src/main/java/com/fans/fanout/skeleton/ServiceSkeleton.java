package com.fans.fanout.skeleton;

/**
 * service 骨架
 *
 * @author ：fsp
 * @date ：2020/4/16 20:23
 */
public class ServiceSkeleton {

    private String name;

    private Integer port;

    private AnatomySkeleton anatomySkeleton;

    private ServiceSkeleton(Builder builder) {
        this.name = builder.name;
        this.port = builder.port;
        this.anatomySkeleton = new AnatomySkeleton(builder.apiClass, builder.implObj);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public AnatomySkeleton getAnatomySkeleton() {
        return anatomySkeleton;
    }

    public void setAnatomySkeleton(AnatomySkeleton anatomySkeleton) {
        this.anatomySkeleton = anatomySkeleton;
    }

    public static class Builder {
        private String name;

        private Integer port;

        private Class apiClass;

        private Object implObj;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder apiClass(Class apiClass) {
            this.apiClass = apiClass;
            return this;
        }

        public Builder implObj(Object implObj) {
            this.implObj = implObj;
            return this;
        }

        public ServiceSkeleton build() {
            return new ServiceSkeleton(this);
        }
    }
}
