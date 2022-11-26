package com.paddi;

import com.paddi.common.GenderEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import com.paddi.utils.mapstruct.UserMapStruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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
                         .username("Paddi-Yan")
                         .password("123456")
                         .gender(GenderEnum.FEMALE)
                         .registerTime(LocalDateTime.now())
                         .build();
        userMapper.insert(user);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        user.getGender().getName();
        System.out.println(userVo);
    }

    @Test
    void friendTest() {
        List<User> userList = friendService.searchFriend("sdaf", 1596190150966370306L);
        userList.forEach(System.out :: println);

    }

    @Resource
    private UserService userService;
    @Test
    void searchUserTest() {
        List<Long> userId = friendMapper.searchByUserId(1596081816133148674L);
        userId.forEach(System.out :: println);
    }

}
