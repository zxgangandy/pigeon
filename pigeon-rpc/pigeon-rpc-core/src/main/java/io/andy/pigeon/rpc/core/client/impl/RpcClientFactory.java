package io.andy.pigeon.rpc.core.client.impl;


import io.andy.pigeon.rpc.core.client.RpcInvokerFactory;
import io.andy.pigeon.rpc.core.client.ConnectClient;
import io.andy.pigeon.rpc.core.client.NettyClient;
import io.andy.pigeon.rpc.core.client.annotation.RpcReference;
import io.andy.pigeon.rpc.core.domain.RpcFutureResponse;
import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.andy.pigeon.rpc.core.serialize.Serializer;
import io.andy.pigeon.rpc.core.util.IpUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class RpcClientFactory extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean {

    private String ip;
    private int port;
    private Serializer serializer = Serializer.SerializeEnum.HESSIAN.getSerializer();
    private RpcInvokerFactory invokerFactory = RpcInvokerFactory.getInstance();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            NettyClient.getInstance().init(IpUtil.getIpPort(ip, port), this.serializer, this.invokerFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        // parse rpcReferenceBean
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(RpcReference.class)) {
                    // valid
                    Class iface = field.getType();
                    if (!iface.isInterface()) {
                        throw new RuntimeException("reference(rpcReference) must be interface.");
                    }

                    Object serviceProxy = RpcClientFactory.this.getObject(iface);

                    // set bean
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);
                }
            }
        });

        return super.postProcessAfterInstantiation(bean, beanName);
    }

    public Object getObject(Class<?> iface) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // method param
                        String className = method.getDeclaringClass().getName();    // iface.getName()
                        String methodName = method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Object[] parameters = args;

                        // request
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(className);
                        request.setMethodName(methodName);
                        request.setParameterTypes(parameterTypes);
                        request.setParameters(parameters);

                        // send
                        // future-response set
                        RpcFutureResponse futureResponse = new RpcFutureResponse(invokerFactory, request);
                        try {
                            // do invoke
                            ConnectClient.asyncSend(request, IpUtil.getIpPort(ip, port));

                            // future get
                            RpcResponse response = futureResponse.get(1000L, TimeUnit.MILLISECONDS);
                            if (response.getErrorMsg() != null) {
                                throw new RuntimeException(response.getErrorMsg());
                            }
                            return response.getResult();
                        } catch (Exception e) {
                            throw (e instanceof RuntimeException) ? e : new RuntimeException(e);
                        }


                    }
                });
    }
}
