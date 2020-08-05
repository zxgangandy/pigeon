package io.andy.pigeon.rpc.server.config;

import io.andy.pigeon.rpc.core.server.RpcServerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 */
@Configuration
public class ProviderConfig {

    @Value("${tcp.port}")
    private int port;

    @Bean
    public RpcServerFactory rpcProviderFactory() {
        RpcServerFactory factory = new RpcServerFactory();
        factory.setPort(port);
        return factory;
    }
}
