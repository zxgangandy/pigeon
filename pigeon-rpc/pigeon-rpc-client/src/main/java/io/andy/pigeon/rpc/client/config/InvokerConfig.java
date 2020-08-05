package io.andy.pigeon.rpc.client.config;

import io.andy.pigeon.rpc.core.client.impl.RpcClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 */
@Configuration
public class InvokerConfig {

    @Value("${tcp.server.adress}")
    private String ip;

    @Value("${tcp.server.port}")
    private int port;

    @Bean
    public RpcClientFactory rpcInvokerFactory() {
        RpcClientFactory factory = new RpcClientFactory();
        factory.setIp(ip);
        factory.setPort(port);
        return factory;
    }
}
