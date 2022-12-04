package com.paddi.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 17:12:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserVo implements Serializable {
    private static final long serialVersionUID = -2205229430625475546L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String gender;

    private String profile;

    private Boolean enabled;

}
