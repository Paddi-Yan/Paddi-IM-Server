package com.paddi.controller;


import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.AddFriendRequestVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Resource
    private FriendService friendService;

    @Resource
    private UserService userService;

    @ResponseBody
    @GetMapping("/search")
    @ApiOperation("根据名称查找好友")
    public Result searchFriend(@RequestParam String searchName, @RequestParam Long userId) {
        List<User> friendList= friendService.searchFriend(searchName, userId);
        if(friendList.isEmpty()) {
            return Result.success(HttpStatusCode.NO_CONTENT, "查询结果为空");
        }
        ArrayList<UserVo> result = new ArrayList<>();
        for(User user : friendList) {
            UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
            result.add(userVo);
        }
        return Result.success(result);
    }

    @ResponseBody
    @GetMapping("/getList")
    @ApiOperation("拉取好友列表")
    public Result getFriendList(@RequestParam Long userId) {
        List<User> friendList = friendService.getFriendList(userId);
        if(friendList.isEmpty()) {
            return Result.success(HttpStatusCode.NO_CONTENT, "好友列表查询结果为空");
        }
        ArrayList<UserVo> result = new ArrayList<>();
        for(User user : friendList) {
            result.add(UserMapStruct.USER_MAPPING.userToUserVo(user));
        }
        return Result.success(result);
    }

    @ResponseBody
    @PostMapping("/addFriend")
    @ApiOperation("发送添加好友请求")
    public Result sendAddFriendRequest(@RequestBody AddFriendRequestVo addFriendRequestVo) {
        UserVo userVo = addFriendRequestVo.getUserVo();
        UserVo receiveUserVo = addFriendRequestVo.getReceiveUserVo();
        HashMap<String, Object> statusInfo = userService.preConditionSearchUser(userVo.getId(), receiveUserVo.getUsername());
        SearchUserStatusEnum status = (SearchUserStatusEnum) statusInfo.get("status");
        if(!SearchUserStatusEnum.SUCCESS.status.equals(status)) {
            return Result.fail(HttpStatusCode.REQUEST_PARAM_ERROR, "发送添加好友请求失败,原因: 携带参数可能错误");
        }
        return Result.success(friendService.sendAddFriendRequest(userVo, receiveUserVo));
    }
}

