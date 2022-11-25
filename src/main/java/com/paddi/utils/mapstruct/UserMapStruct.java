package com.paddi.utils.mapstruct;

import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 17:46:37
 */
@Mapper
public interface UserMapStruct {
    UserMapStruct USER_MAPPING = Mappers.getMapper(UserMapStruct.class);

    @Mapping(target = "gender", source = "gender.name")
    UserVo userToUserVo(User user);
}
