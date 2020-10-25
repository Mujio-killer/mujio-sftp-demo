package com.mujio.sftpDemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: TestController
 * @Author: GZY
 * @Date: 2020/7/25
 */

@RequestMapping("/test")
@Controller
@ResponseBody
public class TestController {
    @RequestMapping("/demo")
    public String test() {
        System.out.println("demo");
        return "测试";
    }
}
