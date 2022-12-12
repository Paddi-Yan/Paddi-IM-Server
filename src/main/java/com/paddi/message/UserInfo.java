package com.paddi.message;

import lombok.*;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月12日 12:29:19
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 9211021982293644249L;

    private String id;

    private String username;

    private String gender;

    private String profile;

    private Integer unreadCount;
}
