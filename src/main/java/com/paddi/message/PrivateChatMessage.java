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

    private String senderId;

    private String senderName;

    private String content;

    private LocalDateTime sendTime;

    private String receiverId;

    private String receiverName;

    @Override
    public int getMessageType() {
        return AbstractMessage.PRIVATE_MESSAGE;
    }
}
