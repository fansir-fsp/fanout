package com.fans.fanout.skeleton;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * service方法包装
 *
 * @author ：fsp
 * @date ：2020/5/20 13:59
 */
public class MethodWrapper {

    private Method method;

    private Map<String, Parameter> parameterMap;

    private Map<String, Integer> parameterIndexMap;

    public MethodWrapper() {
    }

    public MethodWrapper(Method method, Map<String, Parameter> parameterMap, Map<String, Integer> parameterIndexMap) {
        this.method = method;
        this.parameterMap = parameterMap;
        this.parameterIndexMap = parameterIndexMap;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Parameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public Map<String, Integer> getParameterIndexMap() {
        return parameterIndexMap;
    }
}
