package com.paddi.controller;

import com.paddi.common.Result;
import com.paddi.entity.GolangBattle;
import com.paddi.service.GolangService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月07日 17:56:37
 */
@RestController
@RequestMapping("/gobang")
public class GobangController {

    @Resource
    private GolangService golangService;

    @GetMapping("/check/{userId}")
    @ResponseBody
    @ApiOperation("用于重连时检查是否有在游戏对局房间中,有的话直接返回之前的对局房间信息")
    public Result check(@PathVariable Long userId) {
        return Result.success(golangService.check(userId));
    }

    @PostMapping("/create/{userId}")
    @ResponseBody
    @ApiOperation("创建对局")
    public Result create(@PathVariable Long userId) {
        GolangBattle result = golangService.create(userId);
        return Result.success(result);
    }

    @PostMapping("/invite")
    @ResponseBody
    @ApiOperation("邀请好友进入对局")
    public void invite(@RequestParam Long userId, @RequestParam Long friendId) {
        golangService.invite(userId, friendId);
    }
}
