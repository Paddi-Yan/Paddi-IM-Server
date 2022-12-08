package com.paddi.controller;

import com.paddi.common.Result;
import com.paddi.entity.GolangBattle;
import com.paddi.entity.vo.PlayChessRequestVo;
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
    @ApiOperation(value = "检查是否有游戏房间信息", notes = "用于重连时或离开重新进行五子棋界面时检查是否有在游戏对局房间中,有的话直接返回之前的对局房间信息")
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

    @PostMapping("/handleInvitation")
    @ResponseBody
    @ApiOperation(value = "处理游戏邀请", notes = "userId: 处理邀请的用户ID \n inviteUserId: 发送游戏邀请的用户ID \n type: 0-拒绝邀请 1-接收邀请")
    public void handle(@RequestParam Long userId, @RequestParam Long inviteUserId, @RequestParam Integer handleType) {
        golangService.handleInvitation(userId, inviteUserId, handleType);
    }

    @PostMapping("/toBeReady")
    @ResponseBody
    @ApiOperation(value = "准备就绪", notes = "需要两个人都同时就绪才能够开始游戏\n id: 游戏房间编号 \nuserId: 就绪的用户编号")
    public void toBeReady(@RequestParam String id, @RequestParam Long userId) {
        golangService.toBeReady(id, userId);
    }

    @PostMapping("/play")
    @ResponseBody
    @ApiOperation(value = "下棋", notes = "userId: 当前下棋的用户编号 \n [x,y]: 棋子放置在矩阵中的位置 \n golangPiecesType: 黑棋[1]或白棋[-1]")
    public void playChess(@RequestBody PlayChessRequestVo playChessRequestVo) {
        golangService.playChess(playChessRequestVo);
    }

    @PostMapping("/toBeEnd")
    @ResponseBody
    @ApiOperation(value = "结束游戏", notes = "游戏有一方取得胜利后可以选择结束游戏,然后调用该方法删除房间信息,如果不主动调用,30s后也会自动删除\n 可以做重新开始的功能(后话)")
    public void toBeEnd(@RequestParam Long userId) {
        golangService.toBeEnd(userId);
    }
}
