package com.paddi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.paddi.common.GenderEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */

@Data
@TableName("sys_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 814763727885525961L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    private GenderEnum gender;

    private LocalDateTime registerTime;

    private String password;

    private String profile;

    private LocalDateTime lastLoginTime;


}
