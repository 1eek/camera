package com.example.camera.controller;

import com.example.camera.common.Result;
import com.example.camera.service.impl.MsgServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author leek
 * @since 2023-11-07
 */
@RestController
@RequestMapping("/msg")
public class MsgController {

    @Resource
    MsgServiceImpl msgService;

    @GetMapping()
    public Result list(){
        return Result.success(msgService.list());
    }
}
