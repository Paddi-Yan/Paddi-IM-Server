package com.paddi.entity.vo;

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
 * @CreatedTime: 2022年12月04日 21:43:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(value = "ChatHistoryVo-聊天记录请求对象")
public class ChatHistoryVo implements Serializable {

    private static final long serialVersionUID = 1104333245670956109L;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "好友ID或者群聊ID")
    private Long anotherId;

    @ApiModelProperty(value = "当前页数")
    private Long current;

    @ApiModelProperty(value = "查询记录数量")
    private Long size;
}
