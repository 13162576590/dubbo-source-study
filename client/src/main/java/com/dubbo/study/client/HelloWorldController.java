package com.dubbo.study.client;

import com.dubbo.study.IHelloWordService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名称：client
 * 类 名 称：Test
 * 类 描 述：HelloWorldController
 * 创建时间：2020/8/23 11:25 上午
 * 创 建 人：chenyouhong
 */
@RestController
public class HelloWorldController {

    @Reference(timeout=10000000)
    IHelloWordService helloWorldService;

    @GetMapping("/hello")
    public String hello(){
        return helloWorldService.hello("hello world");
    }

}
