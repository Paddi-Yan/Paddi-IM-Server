package com.paddi.entity;

import com.paddi.common.GolangGameStatus;
import com.paddi.entity.vo.UserVo;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月07日 19:00:20
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GolangBattle implements Serializable {

    private static final long serialVersionUID = -8074237961621634472L;

    public static final Integer SIZE = 15;

    private String id;

    private UserVo createUser;

    private UserVo invitedUser;

    private Long whitePiecesUserId;

    private Long blackPiecesUserId;

    private List<Long> readyUserList;

    private int[][] square;

    private GolangGameStatus status;

}
