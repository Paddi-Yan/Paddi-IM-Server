package com.paddi.message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

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
@ApiModel("WebSocket数据帧")
public class Frame implements Serializable {
    private static final long serialVersionUID = 3250130767066001523L;

    @ApiModelProperty("消息序列号")
    private String sequenceId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;

    private String content;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverId;

    private Integer type;

    private Map extend;
}
