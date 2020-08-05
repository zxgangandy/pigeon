package io.andy.pigeon.rpc.core.server;

import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.andy.pigeon.rpc.core.server.annotation.RpcService;
import io.andy.pigeon.rpc.core.serialize.Serializer;
import io.andy.pigeon.rpc.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServerFactory implements ApplicationContextAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(RpcServerFactory.class);

    private Serializer serializer;
    private String ip = IpUtil.getIp();
    private int port;

    private ConcurrentHashMap<String, Object> serviceDataMap = new ConcurrentHashMap<String, Object>();

    private void init() {
        String serializ = Serializer.SerializeEnum.HESSIAN.name();
        Serializer.SerializeEnum serializeEnum = Serializer.SerializeEnum.match(serializ, null);
        this.serializer = serializeEnum != null ? serializeEnum.getSerializer() : null;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (beanMap != null && beanMap.size() > 0) {
            Collection<Object> beans = beanMap.values();
            for (Object bean : beans) {
                String iName = bean.getClass().getInterfaces()[0].getName();
                serviceDataMap.put(iName, bean);
            }
        }
    }

    public ConcurrentHashMap<String, Object> getServiceDataMap() {
        return serviceDataMap;
    }

    @Override
    public void afterPropertiesSet() {
        this.init();
        try {
            NettyServer.class.newInstance().start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addService(String serviceKey, Object bean) {
        this.serviceDataMap.put(serviceKey, bean);
    }

    /**
     * 反射调用服务
     *
     * @param request
     * @return
     */
    public RpcResponse invokeService(RpcRequest request) {

        //  make response
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        // match replicate bean
        String serviceKey = request.getClassName();
        Object serviceBean = serviceDataMap.get(serviceKey);

        // valid
        if (serviceBean == null) {
            response.setErrorMsg("The serviceKey[" + serviceKey + "] not found.");
            return response;
        }

        try {
            // invoke
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            response.setResult(result);
        } catch (Throwable t) {
            // catch error
            logger.error("server invokeService error.", t);
            response.setErrorMsg(t.toString());
        }

        return response;
    }
}
