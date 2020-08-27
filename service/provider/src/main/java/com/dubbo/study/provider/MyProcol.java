package com.dubbo.study.provider;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.RpcException;

/**
 * 项目名称：client
 * 类 名 称：Test
 * 类 描 述：TODO
 * 创建时间：2020/8/23 11:25 上午
 * 创 建 人：chenyouhong
 */
public class MyProcol implements Protocol {
    @Override
    public int getDefaultPort() {
        return 8888;
    }

    //暴露服务（Dubbo-> ；）
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return null;
    }

    @Override
    public void destroy() {

    }

}
