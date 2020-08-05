package io.andy.pigeon.rpc.client;

import io.andy.pigeon.rpc.api.IUserService;
import io.andy.pigeon.rpc.api.domain.User;
import io.andy.pigeon.rpc.core.client.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 用户控制类
 *
 */
@RestController
@RequestMapping
public class UserController {

    @RpcReference
    private IUserService userService;

    @GetMapping("/user")
    public User getUserInfo(Integer userId) {
        User userInfo = userService.getUserInfo(userId);
        return userInfo;
    }

    @GetMapping("/user/batch")
    public String batchInvoke() {
        int count = 0;
        while (true) {
            String requestId = UUID.randomUUID().toString().replace("-", "");
            User userInfo = userService.getUserInfo(1);
            System.out.println(++count + " >>>> " + requestId + "::" + userInfo);
        }
    }

    @GetMapping("/test")
    public String index() {
        return "test:" + System.currentTimeMillis();
    }
}
