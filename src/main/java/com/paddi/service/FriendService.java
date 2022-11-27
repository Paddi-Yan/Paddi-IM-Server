package com.paddi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.paddi.entity.Friend;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
public interface FriendService extends IService<Friend> {

    List<User> searchFriend(String searchName, Long userId);

    List<User> getFriendList(Long userId);

    Boolean sendAddFriendRequest(UserVo userVo , UserVo receiveUserVo, String remark);
}
