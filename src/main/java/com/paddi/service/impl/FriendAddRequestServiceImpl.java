package com.paddi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.paddi.common.FriendRequestStatusEnum;
import com.paddi.entity.Friend;
import com.paddi.entity.FriendAddRecord;
import com.paddi.entity.User;
import com.paddi.exception.BaseException;
import com.paddi.exception.InternalServerException;
import com.paddi.exception.RequestParamValidationException;
import com.paddi.mapper.FriendAddRequestMapper;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.FriendAddRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 20:04:16
 */
@Service
@Transactional(rollbackFor = BaseException.class)
@Slf4j
public class FriendAddRequestServiceImpl extends ServiceImpl<FriendAddRequestMapper, FriendAddRecord> implements FriendAddRequestService {

    @Resource
    private FriendAddRequestMapper friendAddRequestMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FriendMapper friendMapper;

    @Override
    public List<FriendAddRecord> getList(Long id) {
        checkUserById(id);
        List<FriendAddRecord> requestList = friendAddRequestMapper.selectList(id);
        return requestList;
    }

    private void checkUserById(Long id) {
        User user = userMapper.selectById(id);
        if(user == null) {
            throw new RequestParamValidationException(ImmutableMap.of("id", id));
        }
    }

    @Override
    public FriendRequestStatusEnum handleRequest(Long id, Long userId, Integer type) {
        checkUserById(userId);
        FriendAddRecord friendAddRecord = friendAddRequestMapper.selectById(id);
        //接收请求的用户ID不匹配 || 请求已经处理过了
        if(!friendAddRecord.getReceiverId().equals(userId) || !FriendRequestStatusEnum.NOT_HANDLE.getStatus()
                                                                                                 .equals(friendAddRecord.getAccepted())) {
            throw new RequestParamValidationException(ImmutableMap.of("userId", userId, "requestStatus", friendAddRecord.getReceiverId()));
        }
        if(!FriendRequestStatusEnum.REFUSED.status.equals(type) || !FriendRequestStatusEnum.ACCEPTED.status.equals(type)) {
            throw new RequestParamValidationException(ImmutableMap.of("type", type));
        }
        //好友请求
        friendAddRecord.setHandleTime(LocalDateTime.now());
        friendAddRecord.setAccepted(FriendRequestStatusEnum.ACCEPTED.getStatus().equals(type));
        friendAddRequestMapper.updateById(friendAddRecord);
        //拒绝好友请求
        if(!friendAddRecord.getAccepted()) {
            return FriendRequestStatusEnum.REFUSED;
        }
        //同意好友请求更新好友列表
        Friend friend = new Friend(friendAddRecord.getSenderId(), friendAddRecord.getReceiverId());
        int result = friendMapper.insert(friend);
        if(result >= 1) {
            FriendAddRequestServiceImpl.log.info("好友添加成功: {}", friendAddRecord);
        } else {
            throw new InternalServerException();
        }
        return FriendRequestStatusEnum.ACCEPTED;
    }
}
