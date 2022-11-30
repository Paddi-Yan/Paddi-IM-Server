package com.paddi.message;

import com.paddi.common.FrameType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
@Builder
@ApiModel("WebSocket发送的数据帧")
public class Frame implements Serializable {
    private static final long serialVersionUID = 3250130767066001523L;

    @ApiModelProperty("消息序列号")
    private String sequenceId;

    private Long senderId;

    private String content;

    private Long receiverId;

    private FrameType type;

    private Object extend;
}
