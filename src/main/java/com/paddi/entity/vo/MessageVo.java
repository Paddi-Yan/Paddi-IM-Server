package com.paddi.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 22:16:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("MessageVo-消息发送体")
public class MessageVo implements Serializable {
    private static final long serialVersionUID = 7015456036070842371L;

    @ApiModelProperty("发送消息用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;

    @ApiModelProperty("消息内容")
    @JsonSerialize(using = ToStringSerializer.class)
    private String content;

    @ApiModelProperty("可以是好友ID也可以是群聊ID")
    private Long receiverId;
}
