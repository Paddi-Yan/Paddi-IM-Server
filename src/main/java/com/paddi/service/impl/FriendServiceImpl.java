package com.paddi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paddi.entity.Friend;
import com.paddi.entity.User;
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
    public User searchFriend(String searchName, Long userId) {
        //查询出的好友用户信息
        User friend = userService.searchUser(searchName);
        if(friend == null) {
            return null;
        }
        //查询出来的好友关系
        List<Friend> friendList = friendMapper.searchByUserId(userId);
        Friend friendShip = null;
        for(Friend item : friendList) {
            if(item.getFriendId().equals(friend.getId())) {
                friendShip = item;
            }
        }
        if(friendShip == null) {
            return null;
        }
        return friend;
    }

    public boolean checkFriendShip(Friend friend, Long var1, Long var2) {
        Long friendId = friend.getFriendId();
        Long userId = friend.getUserId();
        return (friendId.equals(var1) || friendId.equals(var2)) && (userId.equals(var1) || userId.equals(var2));
    }
}
