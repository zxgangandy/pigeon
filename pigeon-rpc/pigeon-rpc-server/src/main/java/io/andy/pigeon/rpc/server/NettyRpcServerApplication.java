package io.andy.pigeon.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务提供者启动类
 *
 */
@SpringBootApplication
public class NettyRpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyRpcServerApplication.class, args);
    }

}
