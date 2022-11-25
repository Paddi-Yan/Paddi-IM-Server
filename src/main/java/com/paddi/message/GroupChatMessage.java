package com.paddi.message;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 15:28:07
 */
public class GroupChatMessage extends AbstractMessage{
    private static final long serialVersionUID = -3667441796121286633L;

    @Override
    public int getMessageType() {
        return AbstractMessage.GROUP_MESSAGE;
    }
}
