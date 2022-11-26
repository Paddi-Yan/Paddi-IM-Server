package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paddi.entity.Friend;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.mapper.FriendMapper;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private UserService userService;

    @Override
    public List<User> searchFriend(String searchName, Long userId) {
        //查询出来的好友关系
        List<Long> friendIdList = friendMapper.searchByUserId(userId);
        if(friendIdList.isEmpty()) {
            return null;
        }
        List<User> friendList = userService.query()
                                        .getBaseMapper()
                                        .selectList(new QueryWrapper<User>()
                                        .like("username", searchName)
                                        .in("id", friendIdList));
        return friendList;
    }

    @Override
    public List<User> getFriendList(Long userId) {
        List<Long> friendIdList = friendMapper.searchByUserId(userId);
        List<User> friendList = userService.query().getBaseMapper().selectList(new QueryWrapper<User>().in("id", friendIdList));
        return friendList;
    }

    @Override
    public Boolean sendAddFriendRequest(UserVo userVo, UserVo receiveUserVo) {

        return null;
    }
}
