package io.andy.pigeon.store.biz.config.rpc;

import io.andy.pigeon.rpc.core.client.impl.RpcClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SlaveConfig {

    @Value("${master.adress}")
    private String ip;

    @Value("${master.port}")
    private int port;

    @Bean
    public RpcClientFactory rpcClientFactory() {
        RpcClientFactory factory = new RpcClientFactory();
        factory.setIp(ip);
        factory.setPort(port);
        return factory;
    }
}


