package com.paddi.utils.mapstruct;

import com.paddi.entity.FriendAddRecord;
import com.paddi.entity.vo.FriendAddRecordVo;
import com.paddi.entity.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 21:15:07
 */
@Mapper
public interface FriendRecordMapStruct {
    FriendRecordMapStruct REQUEST_MAPPING = Mappers.getMapper(FriendRecordMapStruct.class);


    @Mapping(target = "myself", expression = "java(friendAddRecord.getSenderId().equals(userVo.getId()))")
//    @Mapping(target = "friendInfo", expression = "java(friendAddRecordVo.getSenderId().equals(userVo.getId()) ? friendVo : userVo)")
    @Mapping(target = "id", source = "friendAddRecord.id")
    FriendAddRecordVo entityToVo(FriendAddRecord friendAddRecord, UserVo userVo);
}
