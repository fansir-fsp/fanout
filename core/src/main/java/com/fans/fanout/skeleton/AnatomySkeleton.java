package com.fans.fanout.skeleton;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * service股价详细
 *
 * @author ：fsp
 * @date ：2020/4/18 10:57
 */
public class AnatomySkeleton {

    private Class apiClass;

    private Object implObj;

    Map<String, MethodWrapper> implMethodMap = new HashMap();

    Map<String, MethodWrapper> apiMethodMap = new HashMap();

    public AnatomySkeleton(Class apiClass) {
        this(apiClass, null);
    }

    public AnatomySkeleton(Class apiClass, Object implObj) {
        if (apiClass == null) {
            throw new RuntimeException(
                    MessageFormat.format("配置apiClass不能为空, apiClass:{0}", apiClass));
        }
        this.apiClass = apiClass;
        this.implObj = implObj;
        this.init();
    }

    public synchronized void init() {
        if (implObj != null) {
            implMethodMap = extractMethodWrapperMap(implObj.getClass().getMethods());
        }
        if (apiClass != null) {
            apiMethodMap = extractMethodWrapperMap(apiClass.getMethods());
        }
    }

    public Map<String, MethodWrapper> extractMethodWrapperMap(Method[] methods) {
        Map<String, MethodWrapper> methodMap = new HashMap();
        Arrays.stream(methods).forEach(method -> {
            Map parameterMap = new HashMap<String, Parameter>();
            Map parameterIndexMap = new HashMap<String, Integer>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                String parameterName = parameters[i].getName();
                parameterMap.put(parameterName, parameters[i]);
                parameterIndexMap.put(parameterName, i);
            }
            methodMap.put(method.getName(), new MethodWrapper(method, parameterMap, parameterIndexMap));
        });
        return methodMap;
    }

    public Class getApiClass() {
        return apiClass;
    }

    public Object getImplObj() {
        return implObj;
    }

    public Map<String, MethodWrapper> getImplMethodMap() {
        return implMethodMap;
    }

    public Map<String, MethodWrapper> getApiMethodMap() {
        return apiMethodMap;
    }
}
