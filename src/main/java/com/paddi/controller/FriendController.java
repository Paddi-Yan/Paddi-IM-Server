package com.paddi.controller;


import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.service.FriendService;
import com.paddi.utils.mapstruct.UserMapStruct;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @ResponseBody
    @GetMapping("/search")
    public Result searchFriend(@RequestParam String searchName, @RequestParam Long userId) {
        User friend = friendService.searchFriend(searchName, userId);
        if(friend == null) {
            return Result.success(HttpStatusCode.NO_CONTENT, "查询结果为空");
        }
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(friend);
        return Result.success(userVo);
    }
}

