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
 * @CreatedTime: 2022年11月26日 14:27:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(value = "AddFriendRequestVo-添加好友请求对象")
public class AddFriendRequestVo implements Serializable {
    private static final long serialVersionUID = -341666888507202607L;

    @ApiModelProperty("发送好友请求的用户信息")
    private UserVo userVo;

    @ApiModelProperty("接收好友请求的用户信息")
    private UserVo receiveUserVo;

    @ApiModelProperty("好友添加备注信息")
    private String remark;
}
