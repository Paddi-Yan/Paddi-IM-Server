package com.paddi.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
@Builder
@TableName("sys_private_chat_message")
public class PrivateChatMessage extends AbstractMessage {

    private static final long serialVersionUID = 7235415216025984925L;

    private Long senderId;

    private String content;

    private LocalDateTime sendTime;

    private Long receiverId;

    @TableField(value = "is_read")
    private Boolean alreadyRead;

    @Override
    public int getMessageType() {
        return AbstractMessage.PRIVATE_MESSAGE;
    }
}
