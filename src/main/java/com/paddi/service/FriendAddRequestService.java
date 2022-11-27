package com.paddi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paddi.common.FriendRequestStatusEnum;
import com.paddi.entity.FriendAddRecord;

import java.util.List;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 20:03:50
 */
public interface FriendAddRequestService extends IService<FriendAddRecord> {
    List<FriendAddRecord> getList(Long id);

    FriendRequestStatusEnum handleRequest(Long id, Long userId, Integer type);
}
