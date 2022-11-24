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
    private String senderId;

    private String content;

    private String receiverId;

    private Integer type;
}
