package com.paddi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月12日 15:16:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FriendToCount implements Serializable {
    private static final long serialVersionUID = -7940660785345844267L;

    private Long friendId;

    private Integer unreadMessageCount;
}
