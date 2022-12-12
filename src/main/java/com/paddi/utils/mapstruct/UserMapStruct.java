package com.paddi.utils.mapstruct;

import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.message.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    List<UserVo> userListToUserVoList(List<User> users);

    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserInfo userToUserInfo(User user);

    @Mapping(target = "id", expression = "java(userVo.getId().toString())")
    UserInfo userToUserInfo(UserVo userVo);
}
