package com.paddi.controller;

import com.paddi.common.HttpStatusCode;
import com.paddi.common.Result;
import com.paddi.utils.MinioUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月30日 17:29:37
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    @ResponseBody
    @ApiOperation("上传文件")
    public Result upload(@RequestParam MultipartFile file) {
        try {
            String fileName = minioUtil.upload(file);
            return Result.success(fileName);
        } catch(Exception e) {
            return Result.fail(HttpStatusCode.ERROR, "文件上传出现错误");
        }
    }

    @GetMapping("/download/{fileName}")
    @ResponseBody
    @ApiOperation("根据文件名下载文件")
    public Result download( HttpServletResponse response, @PathVariable String fileName) {
        Boolean isSuccess = minioUtil.download(response, fileName);
        return isSuccess ? Result.success("文件下载成功: "+fileName) : Result.fail(HttpStatusCode.NO_CONTENT, "文件不存在下载失败");
    }
}
