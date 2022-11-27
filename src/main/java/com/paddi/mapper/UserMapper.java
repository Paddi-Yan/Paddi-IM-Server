package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
