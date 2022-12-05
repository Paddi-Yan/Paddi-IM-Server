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
 * @CreatedTime: 2022年12月04日 23:00:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(value = "UpdateUserVo-更改用户信息对象")
public class UpdateUserVo implements Serializable {
    private static final long serialVersionUID = -5336658945614370174L;

    @ApiModelProperty(required = true)
    private Long id;

    @ApiModelProperty(required = true)
    private String username;

    @ApiModelProperty(required = true)
    private String gender;

    @ApiModelProperty(value = "是否需要更改头像",required = true)
    private Boolean needUpdateProfile;
}
