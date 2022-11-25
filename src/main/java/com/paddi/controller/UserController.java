package com.paddi.controller;


import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.entity.User;
import com.paddi.entity.vo.LoginVo;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.service.UserService;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestBody RegisterVo registerVo) {
        if(registerVo.getUsername() == null || registerVo.getPassword() == null || registerVo.getGender() == null) {
            return Result.fail(HttpStatusCode.REQUEST_PARAM_ERROR);
        }
        User user = userService.register(registerVo);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        if(userVo == null) {
            return Result.fail(HttpStatusCode.ERROR, "用户注册失败!");
        }
        return Result.success(userVo);
    }

    @ResponseBody
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        User loginResult = userService.login(loginVo.getUsername(), loginVo.getPassword());
        if(loginResult == null) {
            return Result.fail(HttpStatusCode.REQUEST_PARAM_ERROR, "用户名或密码不正确!");
        }
        return Result.success(loginResult);
    }

    @GetMapping("/search")
    @ResponseBody
    @ApiOperation("根据用户名查询用户")
    public Result searchUser(@RequestParam String username) {
        User user = userService.searchUser(username);
        if(user == null) {
            return Result.success(HttpStatusCode.NO_CONTENT, "查询结果为空");
        }
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        return Result.success(userVo);
    }
}

