package com.paddi;

import com.paddi.common.GenderEnum;
import com.paddi.entity.FriendAddRecord;
import com.paddi.entity.User;
import com.paddi.entity.vo.ChatHistoryVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.mapper.FriendMapper;
import com.paddi.mapper.UserMapper;
import com.paddi.netty.ChatMessageHandler;
import com.paddi.service.ChatService;
import com.paddi.service.FriendAddRequestService;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import com.paddi.utils.MinioUtil;
import com.paddi.utils.mapstruct.UserMapStruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<User> userList = friendService.searchFriend("Pad", 1596190150966370306L);
        List<UserVo> userVoList = UserMapStruct.USER_MAPPING.userListToUserVoList(userList);

    }

    @Resource
    private ChatService chatService;
    @Test
    void chatMessageHistory() {
        ChatHistoryVo chatHistoryVo = new ChatHistoryVo(1L, 2L, 1L, 2L);
        chatService.getPrivateChatHistory(chatHistoryVo).forEach(System.out :: println);
    }

    @Resource
    private UserService userService;

    @Test
    void searchUserTest() {

        List<Long> userId = friendMapper.searchByUserId(1596081816133148674L);
        userId.forEach(System.out :: println);
    }

    @Test
    void enumTest() {
        ChatMessageHandler chatMessageHandler = new ChatMessageHandler();
    }

    @Resource
    private FriendAddRequestService friendAddRequestService;

    @Test
    void test() {
        //Sabrina的好友请求列表
        List<FriendAddRecord> list = friendAddRequestService.getList(1596190150966370306L);
        list.forEach(System.out :: println);
        System.out.println("==============================");
        Long userId = 1596190150966370306L;
        User user = userService.getBaseMapper().selectById(userId);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        System.out.println("以下是" + userVo + "的好友请求列表");
        for(FriendAddRecord friendAddRecord : list) {
            Long queryId = friendAddRecord.getReceiverId()
                                          .equals(userId) ? friendAddRecord.getSenderId() : friendAddRecord.getReceiverId();
            User friend = userService.getBaseMapper().selectById(queryId);
            UserVo friendVo = UserMapStruct.USER_MAPPING.userToUserVo(friend);
            //            FriendAddRecordVo friendAddRecordVo = FriendRecordMapStruct.REQUEST_MAPPING.entityToVo(friendAddRecord, userVo, friendVo);
            //            System.out.println(friendAddRecordVo);
        }
    }

    public static void main(String[] args) {
        //type = 8
        HashMap<String, Integer> friendIdToCount = new HashMap<>();
        ArrayList<Friend> onlineFriendList = new ArrayList<>();
        ArrayList<Friend> notOnlineFriendList = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : friendIdToCount.entrySet()) {
            String friendId = entry.getKey();

            Integer unreadMessageCount = entry.getValue();
            //遍历在线好友列表
            for(Friend friend : onlineFriendList) {
                if(friend.getId().equals(friendId)) {
                    friend.setCount(unreadMessageCount);
                    break;
                }
            }


            //遍历离线好友列表
            for(Friend friend : notOnlineFriendList) {
                if(friend.getId().equals(friendId)) {
                    friend.setCount(unreadMessageCount);
                }
            }
        }
    }
    private class Friend {
        private Long id;
        private Integer count;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }


    @Resource
    private MinioUtil minioUtil;


}
