package com.paddi.controller;


import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.LoginVo;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.service.UserService;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Paddi-Yan
 * @since 2022-11-25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody RegisterVo registerVo) {
        User user = userService.register(registerVo);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        return Result.success(userVo);
    }

    @ResponseBody
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        return Result.success(userService.login(loginVo.getUsername(), loginVo.getPassword()));
    }

    @GetMapping("/search")
    @ResponseBody
    @ApiOperation(value = "根据用户名查询用户", notes = "根据用户名完全匹配-不支持模糊查询-查询结果只有一个或零个")
    public Result searchUser(@RequestParam Long id, @RequestParam String username) {
        HashMap<String, Object> condition = userService.preConditionSearchUser(id, username);
        Integer status = (Integer) condition.get("status");
        if(SearchUserStatusEnum.USER_NOT_EXIST.status.equals(status)
                || SearchUserStatusEnum.YOURSELF.status.equals(status)) {
            return Result.success(HttpStatusCode.NO_CONTENT, "查询结果为空");
        }
        User user = (User) condition.get("user");
        return Result.success(UserMapStruct.USER_MAPPING.userToUserVo(user));
    }
}

