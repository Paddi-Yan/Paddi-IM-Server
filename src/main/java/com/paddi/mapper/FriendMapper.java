package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.Friend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@Mapper
public interface FriendMapper extends BaseMapper<Friend> {

    List<Friend> searchByUserId(Long userId);
}
