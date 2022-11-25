package com.paddi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paddi.entity.User;
import com.paddi.entity.vo.RegisterVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
public interface UserService extends IService<User> {

    /**
     * 检查用户名是否存在
     * @param username
     * @return true-存在/false-不存在
     */
    boolean checkUserNameIsExit(String username);

    User register(RegisterVo registerVo);

    User login(String username, String password);

    User searchUser(String username);
}
