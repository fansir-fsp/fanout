package com.fans.fanout.handler;

import com.fans.fanout.net.Request;
import com.fans.fanout.net.Response;
import com.fans.fanout.serialization.Coder;
import com.fans.fanout.skeleton.MethodWrapper;
import com.fans.fanout.skeleton.ServiceSkeleton;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ：fsp
 * @date ：2020/5/11 17:45
 */
public class RequestHandler {

    Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private ServiceSkeleton serviceSkeleton;

    private Request request;

    public RequestHandler(ServiceSkeleton serviceSkeleton, Request request) {
        this.serviceSkeleton = serviceSkeleton;
        this.request = request;
    }

    public Response doHandler(Coder coder) {

        String reqTicketNO = null;
        try {
            if (request == null) {
                throw new RuntimeException("request为空");
            }
            String reqMethodName = request.getMethodName();
            Map<String, Object> reqParameterInstanceMap = request.getParameterInstanceMap();
            reqTicketNO = request.getTicketNO();
            String reqResultType = request.getResultType();

            MethodWrapper methodWrapper = serviceSkeleton.getAnatomySkeleton().getMethodMap().get(reqMethodName);
            if (methodWrapper == null) {
                //TODO:此处可以细分回包时的错误类型
                throw new RuntimeException("远程方法不存在");
            }

            //check参数类型对应情况
            if (reqParameterInstanceMap.size() != methodWrapper.getParameterMap().size()) {
                //TODO:此处可以细分回包时的错误类型
                throw new RuntimeException("方法参数不对应-参数数量");
            }
            Map<String, Parameter> standardParameterMap = methodWrapper.getParameterMap();
            Set<String> standardParameterKeySet = new HashSet(standardParameterMap.keySet());
            reqParameterInstanceMap.forEach((key, value) -> {
                if (!standardParameterKeySet.contains(key)) {
                    //TODO:此处可以细分回包时的错误类型
                    throw new RuntimeException("方法参数不对应-参数名称");
                }
                if (!value.getClass().isAssignableFrom(standardParameterMap.get(key).getType())) {
                    //TODO:此处可以细分回包时的错误类型
                    throw new RuntimeException("方法参数不对应-参数类型");
                }
            });

            //check返回值类型
            Class returnType = methodWrapper.getMethod().getReturnType();
            if (!Class.forName(reqResultType).isAssignableFrom(returnType)) {
                //TODO:此处可以细分回包时的错误类型
                throw new RuntimeException("方法返回值不对应-返回值类型");
            }

            //参数类型正确的情况下，排序
            Object[] invokeParameters = new Object[reqParameterInstanceMap.size()];
            Map<String, Integer> parameterIndexMap = methodWrapper.getParameterIndexMap();
            parameterIndexMap.forEach((key, value) -> {
                Object obj = reqParameterInstanceMap.get(key);
                invokeParameters[value] = obj;
            });

            //实际方法调用
            Object result = methodWrapper.getMethod()
                    .invoke(serviceSkeleton.getAnatomySkeleton().getImplObj(), invokeParameters);

            //Response回包 - 目前回包类型未作校验，依赖客户端校验
            return coder.getResponses().successResponse(reqTicketNO, result, Class.forName(reqResultType));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解包失败，以系统异常回包 req:{}", request);
            log.error("解包失败，以系统异常回包 error stack:", e);
        }

        if (reqTicketNO == null) {
            //回去的包没有ticket
            return coder.getResponses().systemFailResponse();
        }
        return coder.getResponses().systemFailResponse(reqTicketNO);
    }
}
