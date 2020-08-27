package com.dubbo.study.provider;

import com.dubbo.study.IHelloWordService;
import org.apache.dubbo.config.annotation.Service;

/**
 * 项目名称：client
 * 类 名 称：Test
 * 类 描 述：TODO
 * 创建时间：2020/8/23 11:25 上午
 * 创 建 人：chenyouhong
 */
@Service
public class HelloWordServiceImpl implements IHelloWordService {

    @Override
    public String hello(String content) {
        return "Hello :"+content;
    }

}
