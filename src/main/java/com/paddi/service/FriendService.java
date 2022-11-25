package com.paddi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paddi.entity.Friend;
import com.paddi.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
public interface FriendService extends IService<Friend> {

    User searchFriend(String searchName, Long userId);
}
