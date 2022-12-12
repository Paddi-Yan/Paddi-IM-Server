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
 * @CreatedTime: 2022年12月08日 16:27:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("PlayChessRequestVo-下棋请求对象")
public class PlayChessRequestVo implements Serializable {
    private static final long serialVersionUID = 925956443262324852L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private Integer m;

    private Integer n;

    @ApiModelProperty("棋子类型-[-1: 白棋, 1: 黑棋]")
    private Integer golangPiecesType;

}
