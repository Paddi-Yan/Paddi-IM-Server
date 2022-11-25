package com.paddi;

import com.paddi.common.GenderEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.FriendService;
import com.paddi.utils.mapstruct.UserMapStruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
class PaddiImServerApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private FriendService friendService;

    @Test
    void contextLoads() {
        User user = User.builder()
                         .username("Miko")
                         .password("123456")
                         .gender(GenderEnum.MALE)
                         .registerTime(LocalDateTime.now())
                         .build();
        userMapper.insert(user);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        user.getGender().getName();
        System.out.println(userVo);
    }

    @Test
    void friendTest() {
        User user = friendService.searchFriend("Miko", 1596081816133148674L);
        System.out.println(user);
        System.out.println("===========================================");
        friendMapper.searchByUserId(1596081816133148674L).forEach(System.out :: println);
    }

}
