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
 * @CreatedTime: 2022年11月26日 21:16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("FriendAddRecordVo-好友添加记录对象")
public class FriendAddRecordVo implements Serializable {
    private static final long serialVersionUID = 5132272992369419317L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("用来区分是否是自己发送的好友请求")
    private Boolean myself;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long senderId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiverId;

    @ApiModelProperty("请求处理状态 0：未处理 -1：拒绝 1：接收")
    private Integer status;

    @ApiModelProperty("好友信息")
    private UserVo friendInfo;

    @ApiModelProperty("好友请求备注")
    private String remark;
}
