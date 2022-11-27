package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paddi.common.FriendRequestStatusEnum;
import com.paddi.entity.Friend;
import com.paddi.entity.FriendAddRecord;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.exception.BaseException;
import com.paddi.mapper.FriendMapper;
import com.paddi.service.FriendAddRequestService;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
@Transactional(rollbackFor = BaseException.class)
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private FriendAddRequestService friendAddRequestService;

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
        List<User> friendList = userService.query()
                                           .getBaseMapper()
                                           .selectList(new QueryWrapper<User>().in("id", friendIdList));
        return friendList;
    }

    @Override
    public Boolean sendAddFriendRequest(UserVo userVo, UserVo receiveUserVo, String remark) {
        Integer result = friendAddRequestService.getBaseMapper().selectCount(new QueryWrapper<FriendAddRecord>()
                .eq("sender_id", userVo.getId())
                .eq("receiver_id", receiveUserVo.getId())
                .eq("accepted", FriendRequestStatusEnum.NOT_HANDLE.status));
        if(result >= 1) {
            return false;
        }
        FriendAddRecord request = FriendAddRecord.builder().sendTime(LocalDateTime.now())
                                                 .receiverId(receiveUserVo.getId())
                                                 .senderId(userVo.getId()).remark(remark).build();
        return friendAddRequestService.save(request);
    }
}
