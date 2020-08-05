package io.andy.pigeon.store.biz.config.rpc;

import io.andy.pigeon.rpc.core.server.RpcServerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 */
@Configuration
public class MasterConfig {

    @Value("${master.bind.port}")
    private int port;

    @Bean
    public RpcServerFactory rpcServerFactory() {
        RpcServerFactory factory = new RpcServerFactory();
        factory.setPort(port);
        return factory;
    }
}
