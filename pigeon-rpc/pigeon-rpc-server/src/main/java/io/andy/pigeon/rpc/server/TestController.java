package io.andy.pigeon.rpc.server;

import io.andy.pigeon.rpc.core.server.RpcServerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试控制类
 *
 */
@RestController
@RequestMapping
public class TestController {

    @GetMapping("/test")
    public String index() {
        return System.currentTimeMillis() + "";
    }

    @GetMapping("/test/getServices")
    public Map<String, Object> getServices() {
        RpcServerFactory factory = new RpcServerFactory();
        ConcurrentHashMap<String, Object> serviceDataMap = factory.getServiceDataMap();
        return serviceDataMap;
    }
}
