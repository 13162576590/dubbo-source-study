package com.dubbo.study.provider;

/**
 * 项目名称：client
 * 类 名 称：Test
 * 类 描 述：TODO
 * 创建时间：2020/8/23 11:25 上午
 * 创 建 人：chenyouhong
 */
public class Test1 {

//    public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException {
//        com.dubbo.study.IHelloService w;
//        try {
//            w = ((com.dubbo.study.IHelloService) $1);
//        } catch (Throwable e) {
//            throw new IllegalArgumentException(e);
//        }
//        try {
//            if ("hello".equals($2) && $3.length == 1) {
//                return ($w) w.hello((java.lang.String) $4[0]);
//            }
//        } catch (Throwable e) {
//            throw new java.lang.reflect.InvocationTargetException(e);
//        }
//        throw new org.apache.dubbo.common.bytecode.NoSuchMethodException("Not found method \"" + $2 + "\" in class com.dubbo.study.IHelloService.");
//    }

//package org.apache.dubbo.registry;
//import org.apache.dubbo.common.extension.ExtensionLoader;
//    public class RegistryFactory$Adaptive implements org.apache.dubbo.registry.RegistryFactory {
//        public org.apache.dubbo.registry.Registry getRegistry(org.apache.dubbo.common.URL arg0)  {
//            if (arg0 == null) throw new IllegalArgumentException("url == null");
//            org.apache.dubbo.common.URL url = arg0;
//            String extName = ( url.getProtocol() == null ? "dubbo" : url.getProtocol() );
//            if(extName == null) throw new IllegalStateException("Failed to get extension (org.apache.dubbo.registry.RegistryFactory) name from url (" + url.toString() + ") use keys([protocol])");
//            org.apache.dubbo.registry.RegistryFactory extension = (org.apache.dubbo.registry.RegistryFactory) ExtensionLoader.getExtensionLoader(org.apache.dubbo.registry.RegistryFactory.class).getExtension(extName);
//            return extension.getRegistry(arg0);
//        }
//    }

//    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
//        int length = invokers.size();
//        int totalWeight = 0;
//        boolean sameWeight = true;
//        // 下面这个循环有两个作用，第一是计算总权重 totalWeight，
//        // 第二是检测每个服务提供者的权重是否相同
//        for (int i = 0; i < length; i++) {
//            int weight = getWeight(invokers.get(i), invocation);
//            // 累加权重
//            totalWeight += weight;
//            // 检测当前服务提供者的权重与上一个服务提供者的权重是否相同，
//            // 不相同的话，则将 sameWeight 置为 false。
//            if (sameWeight && i > 0 && weight != getWeight(invokers.get(i - 1), invocation)) {
//                sameWeight = false;
//            }
//        }
//        // 下面的 if 分支主要用于获取随机数，并计算随机数落在哪个区间上
//        if (totalWeight > 0 && !sameWeight) {
//            // 随机获取一个 [0, totalWeight) 区间内的数字
//            int offset = random.nextInt(totalWeight);
//            // 循环让 offset 数减去服务提供者权重值，当 offset 小于0时，返回相应的Invoker。
//            // 举例说明一下，我们有 servers = [A, B, C]，weights = [5, 3, 2]，offset =7。
//            // 第一次循环，offset - 5 = 2 > 0，即 offset > 5，
//            // 表明其不会落在服务器 A 对应的区间上。
//            // 第二次循环，offset-3=-1<0，即 5<offset<8， // 表明其会落在服务器 B 对应的区间上
//            for (int i = 0; i < length; i++) {
//                // 让随机值 offset 减去权重值
//                offset -= getWeight(invokers.get(i), invocation);
//                if (offset < 0) {
//                    // 返回相应的 Invoker
//                    return invokers.get(i);
//                }
//            }
//
//        }
//        // 如果所有服务提供者权重值相同，此时直接随机返回一个即可
//        return invokers.get(ThreadLocalRandom.current().nextInt(length));
//    }

}
