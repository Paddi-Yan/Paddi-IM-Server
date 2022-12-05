package com.paddi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paddi.message.PrivateChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:55:55
 */
@Mapper
public interface PrivateChatMapper extends BaseMapper<PrivateChatMessage> {
    void signMessageAlreadyRead(@Param("userId") Long userId, @Param("friendId") Long friendId);

    List<PrivateChatMessage> getPrivateChatHistory(Page<PrivateChatMessage> page,@Param("userId") Long userId,@Param("friendId") Long friendId);
}
