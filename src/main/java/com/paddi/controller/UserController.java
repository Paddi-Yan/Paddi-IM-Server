package com.paddi.controller;


import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.LoginVo;
import com.paddi.entity.vo.RegisterVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.service.UserService;
import com.paddi.utils.FileSuffixVerificationUtil;
import com.paddi.utils.MinioUtil;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private MinioUtil minioUtil;

    @Value("${minio.profileBucket}")
    private String profileBucketName;

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

    @ResponseBody
    @PostMapping("/common")
    public Result loginOrRegister(@RequestBody LoginVo loginVo) {
        User user = userService.loginOrRegister(loginVo);
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        if(user.getLastLoginTime() == null) {
            return Result.success(userVo, "新用户注册成功!");
        }else {
            return Result.success(userVo, "用户登录成功!");
        }
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

    @PostMapping("/uploadProfile")
    @ResponseBody
    @ApiOperation("上传头像")
    public Result uploadProfile(Long id, MultipartFile file) {
        try {
            String name = file.getOriginalFilename();
            String suffix = name.substring(name.lastIndexOf(".") + 1);
            if(!FileSuffixVerificationUtil.isPhoto(suffix)) {
               return Result.fail(HttpStatusCode.REQUEST_PARAM_ERROR, "图片后缀不合法,上传头像失败!");
            }
            Map<String, String> result = minioUtil.upload(file, profileBucketName);
            String profile = result.get("fileName");
            User user = userService.uploadProfile(id, profile);
            return Result.success(UserMapStruct.USER_MAPPING.userToUserVo(user));
        } catch(Exception e) {
            e.printStackTrace();
            return Result.fail(HttpStatusCode.ERROR, "头像上传失败");
        }
    }
}

