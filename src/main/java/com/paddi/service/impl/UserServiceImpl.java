package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paddi.common.GenderEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.mapper.UserMapper;
import com.paddi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean checkUserNameIsExit(String username) {
        Integer count = userMapper.selectCount(new QueryWrapper<User>().eq("username", username));
        return count.intValue() >= 1;
    }

    @Override
    public User register(RegisterVo registerVo) {
        User user = User.builder().gender(GenderEnum.valueOf(registerVo.getGender()))
                                .username(registerVo.getUsername())
                                .password(registerVo.getPassword())
                                .registerTime(LocalDateTime.now()).build();
        int insert = userMapper.insert(user);
        if(insert == 1) {
            UserServiceImpl.log.info("用户注册成功: {}",user);
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("password", password));
        return user;
    }

    @Override
    public User searchUser(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }
}
