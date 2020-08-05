package io.andy.pigeon.rpc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消费者启动类
 *
 */
@SpringBootApplication
public class NettyRpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyRpcClientApplication.class, args);
    }

}
