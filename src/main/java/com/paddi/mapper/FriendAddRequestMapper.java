package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.entity.FriendAddRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 20:04:56
 */
@Mapper
public interface FriendAddRequestMapper extends BaseMapper<FriendAddRecord> {
    List<FriendAddRecord> selectList(Long id);

}
