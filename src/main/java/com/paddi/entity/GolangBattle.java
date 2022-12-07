package com.paddi.entity;

import com.paddi.common.GolangGameStatus;
import com.paddi.entity.vo.UserVo;
import lombok.*;

import java.io.Serializable;

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

    private static final Integer SIZE = 15;

    private String id;

    private UserVo createUser;

    private UserVo invitedUser;

    private Long whitePiecesUserId;

    private Long blackPiecesUserId;

    private Integer[] square;

    private GolangGameStatus status;

}
