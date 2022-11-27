package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.paddi.common.GenderEnum;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.exception.AuthenticationException;
import com.paddi.exception.BaseException;
import com.paddi.exception.RequestParamValidationException;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Service
@Slf4j
@Transactional(rollbackFor = BaseException.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FriendMapper friendMapper;

    @Override
    public boolean checkUserNameIsExit(String username) {
        Integer count = userMapper.selectCount(new QueryWrapper<User>().eq("username", username));
        return count.intValue() >= 1;
    }

    @Override
    public User register(RegisterVo registerVo) {
        if(registerVo.getUsername() == null || registerVo.getPassword() == null || registerVo.getGender() == null) {
            throw new RequestParamValidationException(ImmutableMap.of("registerInfo", registerVo));
        }
        User user = User.builder().gender(GenderEnum.getGenderEnum(registerVo.getGender()).get())
                        .username(registerVo.getUsername())
                        .password(registerVo.getPassword())
                        .registerTime(LocalDateTime.now()).build();
        int insert = userMapper.insert(user);
        if(insert == 1) {
            UserServiceImpl.log.info("用户注册成功: {}", user);
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("password", password));
        if(user == null) {
            throw new AuthenticationException(ImmutableMap.of("username", username, "password", password));
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User searchUser(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public HashMap<String, Object> preConditionSearchUser(Long id, String searchName) {
        HashMap<String, Object> map = new HashMap<>(2);
        Integer status = null;
        User user = searchUser(searchName);
        //不存在该用户
        if(user == null) {
            status = SearchUserStatusEnum.USER_NOT_EXIST.status;
            map.put("status", status);
            return map;
        }
        //该用户为自己
        if(user.getId().equals(id)) {
            status = SearchUserStatusEnum.YOURSELF.status;
            map.put("status", status);
            return map;
        }
        List<Long> friendIdList = friendMapper.searchByUserId(id);
        //检查是否是好友关系
        for(Long friendId : friendIdList) {
            //已经是好友关系
            if(friendId.equals(user.getId())) {
                status = SearchUserStatusEnum.ALREADY_FRIENDS.status;
                map.put("status", status);
                map.put("user", user);
                return map;
            }
        }
        //非好友关系
        status = SearchUserStatusEnum.SUCCESS.status;
        map.put("status", status);
        map.put("user", user);
        return map;
    }
}
