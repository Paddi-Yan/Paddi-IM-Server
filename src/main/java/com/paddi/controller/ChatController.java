package com.paddi.controller;

import com.paddi.common.Result;
import com.paddi.entity.vo.ChatHistoryVo;
import com.paddi.entity.vo.MessageVo;
import com.paddi.service.ChatService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 15:30:32
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @ResponseBody
    @ApiOperation(value = "返回用户与好友的私聊信息",notes = "类似于分页加载,一次获取一定数量消息")
    @PostMapping("/getPrivateChatHistory")
    public Result getPrivateChatHistory(@RequestBody ChatHistoryVo chatHistoryVo) {
        return Result.success(chatService.getPrivateChatHistory(chatHistoryVo));
    }

    @ResponseBody
    @ApiOperation(value = "发送私信消息")
    @PostMapping("/sendPrivateMessage")
    public Result sendPrivateChatMessage(@RequestBody MessageVo messageVo) {
        chatService.sendPrivateChatMessage(messageVo);
        return Result.success();
    }

    @ResponseBody
    @ApiOperation(value = "发送文件消息")
    @PostMapping("transferFile")
    public Result transferFile(@RequestPart MessageVo messageVo, @RequestPart MultipartFile file) {
        chatService.transferFile(messageVo, file);
        return Result.success();
    }
}
