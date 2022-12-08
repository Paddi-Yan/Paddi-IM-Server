package com.paddi.service;

import com.paddi.entity.GolangBattle;
import com.paddi.entity.vo.PlayChessRequestVo;

import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 00:17:47
 */
public interface GolangService {

    GolangBattle create(Long userId);

    void invite(Long userId, Long friendId);

    Map<String, Object> check(Long userId);

    void handleInvitation(Long userId, Long inviteUserId, Integer handleType);

    void toBeReady(String id, Long userId);

    void playChess(PlayChessRequestVo playChessRequestVo);

    void toBeEnd(Long userId);
}
