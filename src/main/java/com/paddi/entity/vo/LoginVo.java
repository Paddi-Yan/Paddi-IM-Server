package com.paddi.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 00:25:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("LoginVo-用户登录对象")
public class LoginVo implements Serializable {
    private static final long serialVersionUID = 8470818255372966459L;

    private String username;

    private String password;

}
