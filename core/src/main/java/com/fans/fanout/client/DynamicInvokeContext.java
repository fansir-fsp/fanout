package com.fans.fanout.client;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：fsp
 * @date ：2020/5/28 18:05
 */
public class DynamicInvokeContext {

    private final Method method;
    private final Object[] methodArgs;
    private final String methodName;
    private final Map<String, Object> parameterInstanceMap = new HashMap();
    private final String resultTypeName;

    private Pattern invokePattern;

    enum Pattern {
        SYNC, ASYNC
    }

    DynamicInvokeContext(Method method, Object[] methodArgs) {
        this.invokePattern = Pattern.SYNC;
        this.method = method;
        this.methodArgs = methodArgs;
        this.methodName = method.getName();
        this.resultTypeName = method.getReturnType().getName();
        //init 参数map
        Parameter[] parameters = method.getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                this.parameterInstanceMap.put(parameters[i].getName(), methodArgs[i]);
            }
        }
    }

    public String getMethodName() {
        return methodName;
    }

    public Map<String, Object> getParameterInstanceMap() {
        return parameterInstanceMap;
    }

    public String getResultTypeName() {
        return resultTypeName;
    }

    public Pattern getInvokePattern() {
        return invokePattern;
    }

}
