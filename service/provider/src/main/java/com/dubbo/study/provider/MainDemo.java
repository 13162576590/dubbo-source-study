package com.dubbo.study.provider;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Filter;

import java.util.List;

/**
 * 项目名称：client
 * 类 名 称：Test
 * 类 描 述：TODO
 * 创建时间：2020/8/23 11:25 上午
 * 创 建 人：chenyouhong
 */
public class MainDemo {

    public static void main(String[] args) {
//        ExecutorService executor = Executors.newCachedThreadPool();

        //静态扩展点
        //自适应扩展点
        //激活扩展点

//        Protocol protocol=ExtensionLoader.getExtensionLoader(Protocol.class).getExtension("mock");
//        System.out.println(protocol.getDefaultPort());

//        Compiler protocol=ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();
        //.AdaptiveCompiler@1a93a7ca
        URL url=new URL("","",0);
        url=url.addParameter("cache","cache");
        List<Filter> list=ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url,"cache");
        System.out.println(list.size());

    }
}
