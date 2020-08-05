package io.andy.pigeon.rpc.server.impl;

import io.andy.pigeon.rpc.api.IUserService;
import io.andy.pigeon.rpc.api.domain.User;
import io.andy.pigeon.rpc.core.server.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 *
 */
@Service
@RpcService
public class UserServiceImpl implements IUserService {

    /**
     * 模拟用户数据
     *
     * @param id
     * @return
     */
    public User getUserInfo(Integer id) {
        User user = new User();
        user.setId(1);
        user.setUserName("xiao hong");
        return user;
    }
}
