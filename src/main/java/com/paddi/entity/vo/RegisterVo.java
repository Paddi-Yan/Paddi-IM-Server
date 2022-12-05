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
 * @CreatedTime: 2022年11月25日 16:47:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("RegisterVo-用户注册对象")
public class RegisterVo implements Serializable {
    private static final long serialVersionUID = -2867290328348784656L;

    @ApiModelProperty("用户名-不可重复")
    private String username;

    @ApiModelProperty("性别 男/女")
    private String gender;

    @ApiModelProperty("密码")
    private String password;

    //@ApiModelProperty("头像")
    //private String profile;

}
