package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paddi.message.PrivateChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:55:55
 */
@Mapper
public interface PrivateChatMapper extends BaseMapper<PrivateChatMessage> {
}
