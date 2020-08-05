package io.andy.pigeon.rpc.api;


import io.andy.pigeon.rpc.api.domain.User;

/**
 * 用户接口
 *
 */
public interface IUserService {
    User getUserInfo(Integer id);
}
