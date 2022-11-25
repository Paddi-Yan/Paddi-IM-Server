package com.paddi.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 15:45:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Frame implements Serializable {
    private static final long serialVersionUID = 3250130767066001523L;

    private Long senderId;

    private String content;

    private Long receiverId;

    private Integer type;

    private Object extend;
}
