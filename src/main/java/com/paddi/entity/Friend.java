package com.paddi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Getter
@Setter
@TableName("sys_friend")
@ApiModel(value = "Friend对象", description = "")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Friend implements Serializable {

    private static final long serialVersionUID = 5380184219748388602L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long friendId;


}
