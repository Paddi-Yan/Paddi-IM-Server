package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.paddi.common.GenderEnum;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.LoginVo;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.entity.vo.UpdateUserVo;
import com.paddi.exception.AuthenticationException;
import com.paddi.exception.BaseException;
import com.paddi.exception.RequestParamValidationException;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Value("${profile.suffix}")
    private String PROFILE_SUFFIX;

    private static final String DEFAULT_PROFILE = "profile.png";

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
                        .profile(PROFILE_SUFFIX + DEFAULT_PROFILE)
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
        User user = searchUser(searchName);
        return check(id, user);
    }

    @Override
    public HashMap<String, Object> preConditionSearchUser(Long id, Long otherId) {
        User user = userMapper.selectById(otherId);
        return check(id, user);
    }

    @Override
    public User uploadProfile(Long id, String profile) {
        User user = userMapper.selectById(id);
        if(user == null) {
            throw new RequestParamValidationException(ImmutableMap.of("id",id));
        }
        user.setProfile(PROFILE_SUFFIX + profile);
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User loginOrRegister(LoginVo loginVo) {
        String password = loginVo.getPassword();
        String username = loginVo.getUsername();
        if(loginVo == null || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new RequestParamValidationException();
        }
        User user = searchUser(username);
        if(user != null) {
            //登录
            //校验密码是否正确
            if(!user.getPassword().equals(password)) {
                throw new AuthenticationException(ImmutableMap.of("password", password));
            }
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
            return user;
        }else {
            //注册
            User registerInfo = User.builder().username(username)
                             .gender(GenderEnum.UNKNOWN)
                             .registerTime(LocalDateTime.now())
                             .password(password)
                             .profile(PROFILE_SUFFIX + DEFAULT_PROFILE).build();
            userMapper.insert(registerInfo);
            return registerInfo;
        }
    }

    @Override
    public User updateUserInfo(UpdateUserVo updateUserVo) {
        User user = userMapper.selectById(updateUserVo.getId());
        if(user == null) {
            throw new RequestParamValidationException();
        }
        //根据用户名查询出来的用户信息
        User checkUser = searchUser(updateUserVo.getUsername());
        if(checkUser == null) {
            //全新的用户名
            //修改用户名
            user.setUsername(updateUserVo.getUsername());
            user.setGender(GenderEnum.getGenderEnum(updateUserVo.getGender()).get());
        } else if(checkUser.getId().equals(updateUserVo.getId())) {
            //未修改用户名
            user.setGender(GenderEnum.getGenderEnum(updateUserVo.getGender()).get());
        } else if(!checkUser.getId().equals(updateUserVo.getId())) {
            //该用户名已经存在
            throw new RequestParamValidationException(ImmutableMap.of("username", updateUserVo.getUsername(), "cause", "该用户名已经被使用"));
        }
        userMapper.updateById(user);
        return user;
    }

    /**
     *
     * @param id 用户ID
     * @param user 陌生人用户信息
     * @return
     */
    private HashMap<String, Object> check(Long id, User user) {
        User checkUser = userMapper.selectById(id);
        if(checkUser == null) {
            throw new RequestParamValidationException(ImmutableMap.of("cause","ID为"+id+"的用户不存在"));
        }
        HashMap<String, Object> map = new HashMap<>(2);
        Integer status = null;
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
