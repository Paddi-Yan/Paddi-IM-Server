package com.paddi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.paddi.common.FriendRequestStatusEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 14:35:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("sys_friend_add_request")
public class FriendAddRecord implements Serializable {
    private static final long serialVersionUID = 5304800051160343399L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long senderId;

    private Long receiverId;

    private LocalDateTime sendTime;

    private LocalDateTime handleTime;

    private FriendRequestStatusEnum status;

    private String remark;

}
