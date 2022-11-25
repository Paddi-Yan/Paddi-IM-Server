package com.paddi.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 14:27:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PrivateChatMessage extends AbstractMessage {

    private static final long serialVersionUID = 7235415216025984925L;

    private Long senderId;

    private String senderName;

    private String content;

    private LocalDateTime sendTime;

    private Long receiverId;

    private String receiverName;

    private boolean flag;

    @Override
    public int getMessageType() {
        return AbstractMessage.PRIVATE_MESSAGE;
    }
}
