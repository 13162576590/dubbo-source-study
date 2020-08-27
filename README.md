# dubbo-source-study
dubbo源码学习笔记


一、静态扩展点

        Protocol protocol=ExtensionLoader.getExtensionLoader(Protocol.class).getExtension("mock");

        学习源码时，对于包装类如何进行依赖注入没有怎么找到，以mock为例：
        依赖注入在方法createExtension中调用，代码如下:
        //依赖注入代码，wrapper类型class
        //如文件org.apache.dubbo.rpc.Protocol为例，mock就经过多次包装，包装结果是
        //ProtocolListenerWrapper(QosProtocolWrapper(ProtocolFilterWrapper(MockProtocol())))
        instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));

        mock实例包装如mock.png所示

二、自适应扩展点

        Compiler protocol=ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();

        createAdaptiveExtension方法:
        //依赖注入
        //获得一个自适应扩展点的实例
        //newInstance 得到一个AdaptiveExtensionFactory实例，默认构造函数中有相应的逻辑代码，不进入构造函数流程都搞不清
        return injectExtension((T) getAdaptiveExtensionClass().newInstance());


三、激活扩展点

		激活扩展点，只要在url上加上相应的参数，就能激活对应的对象，比如当注释掉如下代码url=url.addParameter("cache","cache")时，相应的list个数是10，取消注释后，相应的list个数是11。

        URL url=new URL("","",0);
        url=url.addParameter("cache","cache");
        List<Filter> list=ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url,"cache");
        System.out.println(list.size());

四、dubbo服务注册源码分析
		dubbo源码服务注册入口在dubbo-config-spring模块下的配置文件spring.handlers所配置的类（DubboNamespaceHandler），配置文件是自定义schema解析

		dubbo入口类DubboNamespaceHandler，初始化方法init()中，及ServiceBean类，如下所示:
        registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(ServiceBean.class, true));


        进入ServiceBean类后，关键入口方法及onApplicationEvent(),如下所示:
       //监听  spring上下文被刷新或者加载的时候触发
	    @Override
	    public void onApplicationEvent(ContextRefreshedEvent event) {
	        if (!isExported() && !isUnexported()) {
	            if (logger.isInfoEnabled()) {
	                logger.info("The service ready on spring started. service: " + getInterface());
	            }
	            export(); //导出、发布
	        }
	    }



		ServiceBean 的实现
		ServiceBean 这个类，分别实现了 InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware, ApplicationEventPublisherAware
		InitializingBean
		接口为 bean 提供了初始化方法的方式，它只包括 afterPropertiesSet 方法，凡是继承该接口的类，在初始化 bean 的时候会执行 该方法。被重写的方法为 afterPropertiesSet
		DisposableBean
		被重写的方法为 destroy,bean 被销毁的时候，spring 容器会自动执行 destory 方法，比如释放资源 
		ApplicationContextAware
		实现了这个接口的 bean，当 spring 容器初始化的时候，会自动的将 ApplicationContext 注入进来 
		ApplicationListener
		ApplicationEvent 事件监听，spring 容器启动后会发一个事件通知。被重写的方法为:onApplicationEvent,onApplicationEvent 方法传入的对象是 ContextRefreshedEvent。这个对象是当 Spring 的上下文被刷新或者加载完毕的时候触发的。因此服务就是在 Spring 的上下文刷新后进行导出操作的
		BeanNameAware
		获得自身初始化时，本身的 bean 的 id 属性，被重写的方法为 setBeanName 
		ApplicationEventPublisherAware
		这个是一个异步事件发送器。被重写的方法为 setApplicationEventPublisher,简单来说，在 spring 里面提供了类似于消息队列 的异步事件解耦功能。(典型的观察者模式的应用)
		spring 事件发送监听由 3 个部分组成 
		1.ApplicationEvent:表示事件本身，自定义事件需要继承该类 
		2.ApplicationEventPublisherAware:事件发送器，需要实现该接口 
		3.ApplicationListener:事件监听器接口


		ServiceConfig类doExportUrls()方法拼接注册url地址,如下:
		registry://127.0.0.1:2181/org.apache.dubbo.registry.RegistryService?application=service-provider&check=true&dubbo=2.0.2&group=dubbo-test&owner=Mic&pid=81443&qos.enable=false&registry=zookeeper&release=2.7.1&simplified=true&timestamp=1597849277739


		registry://127.0.0.1:2181/org.apache.dubbo.registry.RegistryService?application=service-provider&check=true&dubbo=2.0.2&export=dubbo%3A%2F%2F10.116.1.27%3A20881%2Fcom.dubbo.study.IHelloWordService%3Fanyhost%3Dtrue%26application%3Dpractice-service%26bean.name%3Dproviders%3Adubbo%3Acom.dubbo.study.IHelloWordService%26bind.ip%3D10.116.1.27%26bind.port%3D20881%26default.deprecated%3Dfalse%26default.dynamic%3Dfalse%26default.register%3Dtrue%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dfalse%26generic%3Dfalse%26interface%3Dcom.dubbo.study.IHelloWordService%26methods%3DHelloWord%26owner%3DMic%26pid%3D84074%26qos.enable%3Dfalse%26register%3Dtrue%26release%3D2.7.1%26side%3Dprovider%26timestamp%3D1597904516151&group=dubbo-test&owner=Mic&pid=84074&qos.enable=false&registry=zookeeper&release=2.7.1&simplified=true&timestamp=1597904509579



        //此处protocol是Protocol$Adaptive(->做适配使用),Protocol$Adaptive是动态生成的一个代理类，生成代码如下所示
		import org.apache.dubbo.common.extension.ExtensionLoader;

		public class Protocol$Adaptive implements org.apache.dubbo.rpc.Protocol {
		    public void destroy() {
		        throw new UnsupportedOperationException("The method public abstract void org.apache.dubbo.rpc.Protocol.destroy() of interface org.apache.dubbo.rpc.Protocol is not adaptive method!");
		    }

		    public int getDefaultPort() {
		        throw new UnsupportedOperationException("The method public abstract int org.apache.dubbo.rpc.Protocol.getDefaultPort() of interface org.apache.dubbo.rpc.Protocol is not adaptive method!");
		    }

		    public org.apache.dubbo.rpc.Exporter export(org.apache.dubbo.rpc.Invoker arg0) throws org.apache.dubbo.rpc.RpcException {
		        if (arg0 == null) throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument == null");
		        if (arg0.getUrl() == null)
		            throw new IllegalArgumentException("org.apache.dubbo.rpc.Invoker argument getUrl() == null");
		        org.apache.dubbo.common.URL url = arg0.getUrl();

		        //extName=webservice
		        String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
		        if (extName == null)
		            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.rpc.Protocol) name from url (" + url.toString() + ") use keys([protocol])");
		        org.apache.dubbo.rpc.Protocol extension = (org.apache.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(org.apache.dubbo.rpc.Protocol.class).getExtension(extName);

		        return extension.export(arg0);
		    }

		    public org.apache.dubbo.rpc.Invoker refer(java.lang.Class arg0, org.apache.dubbo.common.URL arg1) throws org.apache.dubbo.rpc.RpcException {
		        if (arg1 == null) throw new IllegalArgumentException("url == null");
		        org.apache.dubbo.common.URL url = arg1;
		        String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
		        if (extName == null)
		            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.rpc.Protocol) name from url (" + url.toString() + ") use keys([protocol])");
		        org.apache.dubbo.rpc.Protocol extension = (org.apache.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(org.apache.dubbo.rpc.Protocol.class).getExtension(extName);
		        return extension.refer(arg0, arg1);
		    }
		}

		QosProtocolWrapper -> ProtocolFilterWrapper -> ProtocolListenerWrapper 包装类顺序,但是多次debug发现顺序不一致情况,因为用的上面createExtension代码中cachedWrapperClasses是ConcurrentHashSet维护的。
		实际应用中，以dubbo协议这个扩展为例，当创上述代码建了DubboProtocol协议的instance，此时是DubboProtocol实例，接下来对cachedWrapperClasses进行for循环，创建对应的wrap类，这些wrap类都有一个以Protocol类型的参数，那么创建wrap实例的时候，会把之前产生的Protocol实例传递给他。
		此例，cachedWrapperClasses里的数据来自于META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol文件中配置的是wrap的扩展，
		此处是：
		class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
		class com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper,
		class com.alibaba.dubbo.qos.protocol.QosProtocolWrapper,
		(怎么从配置文件中识别是否是wrap类？答案是 只要这个QosProtocolWrapper类有类型为Protocol的构造函数存在，就当QosProtocolWrapper是wrap类。)
		那么此处便会依次构造这三个类。 这个三个类的构造顺序不固定，因为用的上面createExtension代码中cachedWrapperClasses是ConcurrentHashSet维护的。

        //wrapperInvoker : registry:///
        // RegistryProtocol-> getExtension("registry")
        //ServiceConfig类的doExportUrlsFor1Protocol()方法
        //protocol.export()方法实际调用Protocol$Adaptive的export()方法，然后根据url的参数进行适配，由于url前缀是registry，因此进入是RegistryProtocol类的export()方法
        Exporter<?> exporter = protocol.export(wrapperInvoker);
        exporters.add(exporter);
        protocol.export()类调用顺序是 QosProtocolWrapper -> ProtocolListenerWrapper -> ProtocolFilterWrapper -> RegistryProtocol

        暴露一个本地服务，进入RegistryProtocol类的doLocalExport()方法，该方法源码及分析如下:
        String key = getCacheKey(originInvoker);
        //bounds -chm  ->computeIfAbsent  if(map.get(key)==null){map.put()}
        return (ExporterChangeableWrapper<T>) bounds.computeIfAbsent(key, s -> {
            //orginInvoker->   InvokerDelegate(DelegateProviderMetaDataInvoker(invoker))
            Invoker<?> invokerDelegate = new InvokerDelegate<>(originInvoker, providerUrl);
            //protocol.export -> DubboProtocol.export(本质上就是 暴露一个 20880的端口）
            //protocol-> Protocol$Apaptive -> QosProtocolWrapper(ProtocolListenerWrapper(ProtocolFilterWrapper(DubboProtocol(invoker))))
            //QosProtocolWrapper -> ProtocolFilterWrapper -> ProtocolListenerWrapper -> ProtocolFilterWrapper -> DubboProtocol
            return new ExporterChangeableWrapper<>((Exporter<T>) protocol.export(invokerDelegate), originInvoker);
        });

		//invoker -> 代理类
		Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addParameterAndEncoded(EXPORT_KEY, url.toFullString()));
		//MetaData元数据的委托
		DelegateProviderMetaDataInvoker wrapperInvoker = new DelegateProviderMetaDataInvoker(invoker, this);


		//DubboProtocol类openServer(url)方法开启一个服务，暴露20880端口,底层使用netty4启动一个服务
        openServer(url); 

        netty服务启动后，需要把注册到zookeeper
        // 把dubbo:// url注册到zk上
        final Registry registry = getRegistry(originInvoker);


        //RegistryProtocol类的register(URL registryUrl, URL registeredProviderUrl)方法
        //registryFactory实际是registryFactory$Adaptive(->做适配使用),url如下zookeeper://
        //该方法进入AbstractRegistryFactory类的getRegistry(URL url)方法
        registryFactory.getRegistry(registryUrl);

        //zookeeper://127.0.0.1:2181/org.apache.dubbo.registry.RegistryService?application=practice-service&check=true&dubbo=2.0.2&export=dubbo%3A%2F%2F127.0.0.1%3A20881%2Fcom.dubbo.study.IHelloWordService%3Fanyhost%3Dtrue%26application%3Dpractice-service%26bean.name%3Dproviders%3Adubbo%3Acom.dubbo.study.IHelloWordService%26bind.ip%3D127.0.0.1%26bind.port%3D20881%26default.deprecated%3Dfalse%26default.dynamic%3Dfalse%26default.register%3Dtrue%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dfalse%26generic%3Dfalse%26interface%3Dcom.dubbo.study.IHelloWordService%26methods%3DHelloWord%26owner%3DMic%26pid%3D7868%26qos.enable%3Dfalse%26register%3Dtrue%26release%3D2.7.1%26side%3Dprovider%26timestamp%3D1598019896390&group=dubbo-test&owner=Mic&pid=7868&qos.enable=false&release=2.7.1&simplified=true&timestamp=1598019896387


        //AbstractRegistryFactory类getRegistry(URL url)方法
        //create registry by spi/ioc，进入ZookeeperRegistryFactory类
        registry = createRegistry(url);

        ZookeeperRegistry
		这个方法中使用了 CuratorZookeeperTransport 来实现 zk 的连接

        //建立连接。底层通过curator和zookeeper建立连接
        this.zkClient = zookeeperTransporter.connect(url);


        //类RegistryProtocol方法register(URL registryUrl, URL registeredProviderUrl)
	    public void register(URL registryUrl, URL registeredProviderUrl) {
	        Registry registry = this.registryFactory.getRegistry(registryUrl);
	        //这一行看了半天没怎么看懂，debbug源码发现registry是一个zookeeperRegistry,但是实现方法并没有这个类，只好再次进入上一行代码方法继续跟踪，发现返回的registry实际是ZookeeperRegistry并且继承了FailbackRegistry
	        //class ZookeeperRegistry extends FailbackRegistry
	        //最终进入FailbackRegistry类的register()方法
	        registry.register(registeredProviderUrl);
	    }

	    //FailbackRegistry类register(URL url)方法
	    //ZookeeperRegistry类的doRegister(url)方法
        // Sending a registration request to the server side
        doRegister(url)

五、dubbo服务发现源码分析

	注解的方式的初始化入口是
	ReferenceAnnotationBeanPostProcessor -> ReferenceBeanInvocationHandler.init -> ReferenceConfig.get() 获得一个远程代理类
	ReferenceBeanInvocationHandler是ReferenceAnnotationBeanPostProcessor的一个内部类


	//ReferenceConfig类的get()方法
	T get();

	//ReferenceConfig类 get() -> init() -> createProxy(Map<String, String> map)

	//createProxy(Map<String, String> map)方法调用以下方法,REF_PROTOCOL是Protocol$Adaptive(->做适配使用),Protocol$Adaptive是动态生成的一个代理类
	//REF_PROTOCOL.refer调用顺序是(顺序会有变动，多次debug发现顺序不一致情况,因为用的上面createExtension代码中cachedWrapperClasses是ConcurrentHashSet维护的) QosProtocolWrapper -> ProtocolListenerWrapper -> ProtocolFilterWrapper -> RegistryProtocol
	REF_PROTOCOL.refer(interfaceClass, urls.get(0))

	//RegistryProtocol类的refer(Class<T> type, URL url)

	//调用RegistryProtocol类的register(URL registryUrl, URL registeredProviderUrl)方法
    //registryFactory实际是registryFactory$Adaptive(->做适配使用),url如下zookeeper://
    //该方法进入AbstractRegistryFactory类的getRegistry(URL url)方法
    registryFactory.getRegistry(registryUrl);

    然后调用RegistryProtocol类doRefer(Cluster cluster, Registry registry, Class<T> type, URL url)方法
    组装一个RegistryDirectory，走到registry.register(directory.getRegisteredConsumerUrl());此处registry是一个zookeeperRegistry,但是对应的实现并没有这个类，由上一个方法入参传入registry实际是ZookeeperRegistry并且继承了FailbackRegistry，故进入FailbackRegistry类的register(URL url)
	class ZookeeperRegistry extends FailbackRegistry

	//订阅注册中心指定节点的变化，如果发生变化，则通知到RegistryDirectory。Directory其实和服务的注 册以及服务的发现有非常大的关联.
    directory.subscribe(subscribeUrl.addParameter(CATEGORY_KEY,
        PROVIDERS_CATEGORY + "," + CONFIGURATORS_CATEGORY + "," + ROUTERS_CATEGORY));


    //RegistryDirectory类subscribe(URL url)方法
    //registry.subscribe(url, this);此处registry是一个class ZookeeperRegistry extends FailbackRegistry

    //FailbackRegistry类subscribe(URL url, NotifyListener listener)方法
    // Sending a subscription request to the server side
    doSubscribe(url, listener);

    //ZookeeperRegistry类doSubscribe(final URL url, final NotifyListener listener)方法
    //调用notify进行通知，对已经可用的列表进行通知
    //this.notify(url, listener, urls); 

    //FailbackRegistry类notify(URL url, NotifyListener listener, List<URL> urls)方法
    //this.doNotify(url, listener, urls);
    //super.notify(url, listener, urls);

    //AbstractRegistry类notify(URL url, NotifyListener listener, List<URL> urls)方法
    //listener.notify(categoryList); //消费端的listener是最开始传递过来的RegistryDirectory，所以这里会触发RegistryDirectory.notify RegistryDirectory.notify

    //RegistryDirectory类void notify(List<URL> urls)方法

    // 如果router 路由节点有变化，则从新将router 下的数据生成router
	List<URL> routerURLs = categoryUrls.getOrDefault(ROUTERS_CATEGORY, Collections.emptyList());
	toRouters(routerURLs).ifPresent(this::addRouters);
	// 获得provider URL，然后调用refreshOverrideAndInvoker进行刷新
	List<URL> providerURLs = categoryUrls.getOrDefault(PROVIDERS_CATEGORY, Collections.emptyList());
    refreshOverrideAndInvoker(providerURLs);

    //RegistryDirectory类vrefreshOverrideAndInvoker(List<URL> urls)方法

	//逐个调用注册中心里面的配置，覆盖原来的url，组成最新的url 放入overrideDirectoryUrl 存储 根据 provider urls，重新刷新Invoker
    //RegistryDirectory类refreshInvoker(List<URL> invokerUrls)方法

    //RegistryDirectory类Map<String, Invoker<T>> toInvokers(List<URL> urls)方法
    Map<String, Invoker<T>> newUrlInvokerMap = toInvokers(invokerUrls);// Translate url list to Invoker map

    //此处protocol是一个Protocol$Adaptive是动态生成的一个代理类,QosProtocolWrapper -> ProtocolListenerWrapper -> ProtocolFilterWrapper -> DubboProtocol
    //url: dubbo://ip:port
	invoker = new RegistryDirectory.InvokerDelegate(this.protocol.refer(this.serviceType, url), url, providerUrl);

	最终进入DubboProtocol类的<T> Invoker<T> refer(Class<T> serviceType, URL url) throws RpcException()方法

	//DubboProtocol类ExchangeClient[] getClients(URL url)方法 获得客户端连接的方法

	//获得一个共享连接,默认是共享同一个连接进行通信
	//DubboProtocol类List<ReferenceCountExchangeClient> getSharedClient(URL url, int connectNum)方法

	//如果当前消费者还没有和服务端产生连接，则初始化
	clients = buildReferenceCountExchangeClientList(url, connectNum);

	//根据连接数配置，来构建指定个数的链接。默认为1
	List<ReferenceCountExchangeClient> buildReferenceCountExchangeClientList(URL url, int connectNum)

	ReferenceCountExchangeClient buildReferenceCountExchangeClient(URL url)

	//进入到初始化客户端连接的方法了,根据url中配置的参数进行远程通信的构建
    ExchangeClient exchangeClient = initClient(url);

    //Exchangers类ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException方法
    //创建一个客户端连接
    client = Exchangers.connect(url, requestHandler);

    //得到一个Exchanger$Adaptive(->做适配使用),实际是一个HeaderExchanger,因为扩展点的默认值是header
    getExchanger(url)

    //HeaderExchanger类ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException方法
    //getExchanger(url).connect(url, handler)

    //HeaderExchanger类ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException方法
    //return new HeaderExchangeClient(Transporters.connect(url, new DecodeHandler(new HeaderExchangeHandler(handler))), true);

    //Transporters类Client connect(URL url, ChannelHandler... handlers) throws RemotingException方法
    //return getTransporter().connect(url, handler);

    //NettyTransporter类Client connect(URL url, ChannelHandler listener) throws RemotingException
    //return new NettyClient(url, listener); //使用netty构建了一个客户端连接,默认使用netty4

    NettyClient类构造函数继续调用父类AbstractClient的构造函数,在构造函数中调用了doOpen()方法
    消费端请求、响应在NettyClientHandler类中

    消费端请求NettyClientHandler类void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception方法把数据发出去
    消费端响应NettyClientHandler类void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception方法读取响应数据

    //Cluster$AAdaptive类代码
    public class Cluster$Adaptive implements org.apache.dubbo.rpc.cluster.Cluster {
        public org.apache.dubbo.rpc.Invoker join(org.apache.dubbo.rpc.cluster.Directory arg0) throws org.apache.dubbo.rpc.RpcException {
            if (arg0 == null)
                throw new IllegalArgumentException("org.apache.dubbo.rpc.cluster.Directory argument == null");
            if (arg0.getUrl() == null)
                throw new IllegalArgumentException("org.apache.dubbo.rpc.cluster.Directory argument getUrl() == null");
            org.apache.dubbo.common.URL url = arg0.getUrl();
            String extName = url.getParameter("cluster", "failover");
            if (extName == null) throw new IllegalStateException("Failed to get
                    extension(org.apache.dubbo.rpc.cluster.Cluster)name from url(" + url.toString() + ")use keys([cluster])
            ");
            org.apache.dubbo.rpc.cluster.Cluster extension = (org.apache.dubbo.rpc.cluster.Cluster) ExtensionLoader.getExtensionLoader(org.apa che.dubbo.rpc.cluster.Cluster.class).getExtension(extName);
            return extension.join(arg0);
        }
    }

    //此处cluster是一个Cluster$AAdaptive是动态生成的一个代理类,并且在文件org.apache.dubbo.rpc.cluster.Cluster中，有一个包装类mock=org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterWrapper
    //因此顺序 MockClusterWrapper -> FailoverCluster
    cluster.join(directory);

    拿到invoker，invoker包含2个属性，一个是directory: RegistryDirectory,另一个是invoker: FailoverClusterInvoker

    private static final ProxyFactory proxyFactory = (ProxyFactory)ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    //proxyFactory创建一个代理对象,此处的proxyFactory是一个ProxyFactory$Adaptive(->做适配使用),ProxyFactory$Adaptive是动态生成的一个代理类,并且在文件org.apache.dubbo.rpc.ProxyFactory中，有一个包装类stub=org.apache.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper
    //因此顺序 StubProxyFactoryWrapper -> JavassistProxyFactory
    //proxyFactory.getProxy(this.invoker); 

六、dubbo消费端通信

	消费者初始化完成之后，会生成一个proxy，而这个proxy本质上是一个动态代理类。
	JavassistProxyFactory.getProxy
	invoker实际上是:MockClusterWrapper(FailoverCluster(directory)) 然后通过InvokerInvocationHandler做了一层包装变成了 InvokerInvocationHandler(MockClusterWrapper(FailoverCluster(directory)))
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
    	return Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    //类Proxy类getProxy(Class<?>... ics)方法生成动态代理(@Reference代理类)
    //Proxy.getProxy();

    //Proxy getProxy(ClassLoader cl, Class<?>... ics)方法
    //ccp = ClassGenerator.newInstance(cl);
    生成代理类方法在变量ccp的mMethods属性上


    消费端调用的过程
    接口调用先进入InvokerInvocationHandler类Object invoke(Object proxy, Method method, Object[] args) throws Throwable方法
	此处invoker是InvokerInvocationHandler(MockClusterWrapper(FailoverCluster(directory)))

	//MockClusterWrapper类Result invoke(Invocation invocation) throws RpcException方法
    result = this.invoker.invoke(invocation);

	//AbstractClusterInvoker类Result invoke(Invocation invocation) throws RpcException方法
    result = this.invoker.invoke(invocation);

    //获取invokers
    List<Invoker<T>> invokers = this.list(invocation);
    return this.directory.list(invocation);

    //AbstractDirectory类List<Invoker<T>> list(Invocation invocation) throws RpcException方法
    return this.doList(invocation);

    //RegistryDirectory类List<Invoker<T>> doList(Invocation invocation)方法

    //初始化负载均衡，选择负载均衡算法
    LoadBalance loadbalance = this.initLoadBalance(invokers, invocation);

    //ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension()此处是一个自适应扩展点，使用LoadBalance$Adaptive做适配,默认是随机算法,因此得到的LoadBalance是RadomLoadBalance
    return ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(invokers.get(0).getUrl().getMethodParameter(RpcUtils.getMethodName(invocation), LOADBALANCE_KEY, DEFAULT_LOADBALANCE));

    //FailoverClusterInvoker类Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException方法
    return this.doInvoke(invocation, invokers, loadbalance);

	//FailoverClusterInvoker继承FailoverCluster
    class FailoverClusterInvoker<T> extends AbstractClusterInvoker<T>

	//AbstractClusterInvoker类Invoker<T> select(LoadBalance loadbalance, Invocation invocation, List<Invoker<T>> invokers, List<Invoker<T>> selected) throws RpcException方法,通过负载均衡或得一个目标invoker
    Invoker<T> invoker = this.select(loadbalance, invocation, copyInvokers, invoked);

    //选择目标invoker
    Invoker<T> invoker = this.doSelect(loadbalance, invocation, invokers, selected);

    //AbstractLoadBalance类<T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation)方法
    Invoker<T> invoker = loadbalance.select(invokers, this.getUrl(), invocation);

    //调用 doSelect 方法进行负载均衡，该方法为抽象方法，由子类实现,此处实现类是RandomLoadBalance,因此进入RandomLoadBalance类<T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation)方法
    return doSelect(invokers, url, invocation);

    //负载均衡随机算法
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int length = invokers.size();
        int totalWeight = 0;
        boolean sameWeight = true;
        // 下面这个循环有两个作用，第一是计算总权重 totalWeight，
        // 第二是检测每个服务提供者的权重是否相同
        for (int i = 0; i < length; i++) {
            int weight = getWeight(invokers.get(i), invocation);
            // 累加权重
            totalWeight += weight;
            // 检测当前服务提供者的权重与上一个服务提供者的权重是否相同，
            // 不相同的话，则将 sameWeight 置为 false。
            if (sameWeight && i > 0 && weight != getWeight(invokers.get(i - 1), invocation)) {
                sameWeight = false;
            }
        }
        // 下面的 if 分支主要用于获取随机数，并计算随机数落在哪个区间上
        if (totalWeight > 0 && !sameWeight) {
            // 随机获取一个 [0, totalWeight) 区间内的数字
            int offset = random.nextInt(totalWeight);
            // 循环让 offset 数减去服务提供者权重值，当 offset 小于0时，返回相应的Invoker。
            // 举例说明一下，我们有 servers = [A, B, C]，weights = [5, 3, 2]，offset =7。
            // 第一次循环，offset - 5 = 2 > 0，即 offset > 5，
            // 表明其不会落在服务器 A 对应的区间上。
            // 第二次循环，offset-3=-1<0，即 5<offset<8， // 表明其会落在服务器 B 对应的区间上
            for (int i = 0; i < length; i++) {
                // 让随机值 offset 减去权重值
                offset -= getWeight(invokers.get(i), invocation);
                if (offset < 0) {
                    // 返回相应的 Invoker
                    return invokers.get(i);
                }
            }

        }
        // 如果所有服务提供者权重值相同，此时直接随机返回一个即可
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }


    此处得到invoker是一个:InvokerDelegate(ProtocolFilterWrapper(ListenerInvokerWrapper(DubboInvoker())
    初始化invoker在RegistryDirectory.notify->refreshInvoker->toInvokers 
    invoker = new InvokerDelegate<>(protocol.refer(serviceType, url), url, providerUrl);
	protocol.refer来构建的invoker，并且使用InvokerDelegate进行了委托,在dubboprotocol中， 是这样构建invoker的。返回的是一个DubboInvoker

	//InvokerDelegate继承InvokerWrapper
	class InvokerDelegate<T> extends InvokerWrapper<T> 

	因此回到FailoverClusterInvoker类的Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException方法的Result result = invoker.invoke(invocation)时,应进入InvokerWrapper类的Result invoke(Invocation invocation) throws RpcException方法

	进入类InvokerWrapper类Result invoke(Invocation invocation) throws RpcException方法
	Result result = invoker.invoke(invocation);

	ListenerInvokerWrapper类Result invoke(Invocation invocation) throws RpcException方法
	return this.invoker.invoke(invocation);

	进入ProtocolFilterWrapper类invoke()方法,进入ProtocolFilterWrapper类后，发现没有invoke()方法
	因为Filter是一个过滤链，由一系列的Filter组成
	所有的Filter在文件org.apache.dubbo.Filter中，比如ConsumerContextFilter就是一个消费端的激活扩展点，激活后就会生效
	@Activate(group = {"consumer"}, order = -10000)
	public class ConsumerContextFilter implements Filter {

	}

	AbstractInvoker类Result invoke(Invocation inv) throws RpcException方法

	DubboInvoker类Result doInvoke(Invocation invocation) throws Throwable方法
    return this.doInvoke(invocation);

    //此处currentClient实际是一个ReferenceCountExchangeClient(HeaderExchangeClient())
	//所以它的调用链路是ReferenceCountExchangeClient->HeaderExchangeClient->HeaderExchangeChannel->(request方法)
	//ReferenceCountExchangeClient类ResponseFuture request(Object request, int timeoutf) throws RemotingException方法
    CompletableFuture<Object> responseFuture = currentClient.request(inv, timeout);

	//HeaderExchangeClient类CompletableFuture<Object> request(Object request, int timeout) throws RemotingException方法
    return client.request(request, timeout);

	//HeaderExchangeChannel类CompletableFuture<Object> request(Object request, int timeout) throws RemotingException方法
	return channel.request(request, timeout);

	此处channel的调用链路 AbstractPeer.send -> AbstractClient.send -> NettyChannel.send 通过NioSocketChannel把消息发送出去
	ChannelFuture future = channel.writeAndFlush(message);


六、dubbo服务端通信

	dubbo底层是基于netty通信,会启动一个netty服务。NettyServer类doOpen()方法就是启动一个netty服务。

	源码入下:
	@Override
    protected void doOpen() throws Throwable {
        bootstrap = new ServerBootstrap();

        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        workerGroup = new NioEventLoopGroup(getUrl().getPositiveParameter(IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS),
                new DefaultThreadFactory("NettyServerWorker", true));

        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getUrl(), this);
        channels = nettyServerHandler.getChannels();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // FIXME: should we use getTimeout()?
                        int idleTimeout = UrlUtils.getIdleTimeout(getUrl());
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyServer.this);
                        ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, idleTimeout, MILLISECONDS))
                                .addLast("handler", nettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();

    }

	Handler与Servlet中的filter很像，通过Handler可以完成通讯报文的解码编码、拦截指定的报文、统一 对日志错误进行处理、统一对请求进行计数、控制Handler执行与否
	当消费端请求时，hander会进行处理，重点关注pipeline的addLast("handler", nettyServerHandler)，此处handler配置的是nettyServerHandler。
	消费端连接服务端,进入NettyServerHandler类的void channelActive(ChannelHandlerContext ctx) throws Exception方法,激活channel

	当服务端收到读的请求是，会进入NettyServerHandler类的void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception方法。 
	handler.channelRead()

	接着通过handler.received来处理msg，这个handle的链路很长
	handler的调用链路
	handler -> MultiMessageHandler -> HeartbeatHandler -> AllChannelHandler -> DecodeHandler -> HeaderExchangeHandler -> DubboProtocol$requestHandler(receive)
	租后一个链路是因为在服务启动一个服务时，创建一个requestHandler,该requestHandler在DubboProtocol类中创建,ExchangeHandler requestHandler = new ExchangeHandlerAdapter() { }。

	MultiMessageHandler: 复合消息处理
	HeartbeatHandler:心跳消息处理，接收心跳并发送心跳响应
	AllChannelHandler:业务线程转化处理器，把接收到的消息封装成ChannelEventRunnable可执行任务给线程池处理
	DecodeHandler:业务解码处理器

	handler的包装在NettyServer类的构造方法包装,构成方法如下:
    public NettyServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, ChannelHandlers.wrap(handler, ExecutorUtil.setThreadName(url, SERVER_THREAD_POOL_NAME)));
    }

    进入ChannelHandlers类的ChannelHandler wrap(ChannelHandler handler, URL url)方法,在进入方法ChannelHandler wrapInternal(ChannelHandler handler, URL url)方法。

    protected ChannelHandler wrapInternal(ChannelHandler handler, URL url) {
    	return new MultiMessageHandler(new HeartbeatHandler(ExtensionLoader.getExtensionLoader(Dispatcher.class)
            .getAdaptiveExtension().dispatch(handler, url)));
    }

	ExtensionLoader.getExtensionLoader(Dispatcher.class).getAdaptiveExtension().dispatch(handler, url)
    AllDispatcher类ChannelHandler dispatch(ChannelHandler handler, URL url)方法

    到目前为止，handler的链路到了AllChannelHandler,但是还有2个链路并没有包装进来，这时继续寻找服务端启动一个服务，初始化initClient时，会对handler进行包装，DubboProtocol类ExchangeClient initClient(URL url)方法
    进入Exchangers类ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException方法，最终进入HeaderExchanger类ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException方法。源码如下:
    @Override
    public ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException {
        return new HeaderExchangeClient(Transporters.connect(url, new DecodeHandler(new HeaderExchangeHandler(handler))), true);
    }

    因此最终组成了handler的调用链路。

    消费端请求到服务端时，服务端收到读的请求是，会进入NettyServerHandler类void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception这个方法。

    //进入MultiMessageHandler类void received(Channel channel, Object message) throws RemotingException方法
    handler.received(channel, msg);

    //HeartbeatHandler类void received(Channel channel, Object message) throws RemotingException方法
    handler.received(channel, obj);

    //AllChannelHandler类void received(Channel channel, Object message) throws RemotingException方法
    handler.received(channel, obj);

    //ChannelEventRunnable类void run()方法
    handler.received(channel, obj);

    //DecodeHandler类void received(Channel channel, Object message) throws RemotingException方法
    handler.received(channel, message);

    //HeaderExchangeHandler类void received(Channel channel, Object message) throws RemotingException方法
	handler.received(channel, message);
    
    交互层请求响应处理，有三种处理方式 
    1. handlerRequest，双向请求
	2. handler.received 单向请求
	3. handleResponse 响应消息

	源码如下:
	@Override
    public void received(Channel channel, Object message) throws RemotingException {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        final ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            if (message instanceof Request) {
                // handle request.
                Request request = (Request) message;
                if (request.isEvent()) {
                    handlerEvent(channel, request);
                } else {
                    if (request.isTwoWay()) {
                        handleRequest(exchangeChannel, request);
                    } else {
                        handler.received(exchangeChannel, request.getData());
                    }
                }
            } else if (message instanceof Response) {
                handleResponse(channel, (Response) message);
            } else if (message instanceof String) {
                if (isClientSide(channel)) {
                    Exception e = new Exception("Dubbo client can not supported string message: " + message + " in channel: " + channel + ", url: " + channel.getUrl());
                    logger.error(e.getMessage(), e);
                } else {
                    String echo = handler.telnet(channel, (String) message);
                    if (echo != null && echo.length() > 0) {
                        channel.send(echo);
                    }
                }
            } else {
                handler.received(exchangeChannel, message);
            }
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }


    进入void handleRequest(final ExchangeChannel channel, Request req) throws RemotingException 方法
    handleRequest(exchangeChannel, request);

	CompletionStage<Object> future = handler.reply(channel, msg);
	由链路组成知道，最终reply的方法实际进入DubboProtocol类中requestHandler的reply方法

	AbstractProxyInvoker类Result invoke(Invocation invocation) throws RpcException方法,此处invoker是服务法布时包装的一个 ProtocolFilterWrapper(InvokerDelegate(DelegateProviderMetaDataInvoker(AbstractProxyInvoker)))
    Result result = invoker.invoke(inv);

    JavassistProxyFactory类<T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url)方法，实际是AbstractProxyInvoker类Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable方法。
    Object value = doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());

    return new AbstractProxyInvoker<T>(proxy, type, url) {
        protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
            return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
        }
    };

    此处wrapper是一个动态代理类，它是动态生成,最终调用动态生成的方法，而非反射调用，生成源码方法源码如下:
    public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException {
        com.dubbo.study.IHelloWordService w;
        try {
            w = ((com.dubbo.study.IHelloWordService) $1);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        try {
            if ("hello".equals($2) && $3.length == 1) {
                return ($w) w.hello((java.lang.String) $4[0]);
            }
        } catch (Throwable e) {
            throw new java.lang.reflect.InvocationTargetException(e);
        }
        throw new org.apache.dubbo.common.bytecode.NoSuchMethodException("Not found method \"" + $2 + "\" in class com.dubbo.study.IHelloWordService.");
    }

    调用结束回到HeaderExchangeHandler类reply()之后继续执行,进入HeaderExchangeChannel类void send(Object message, boolean sent) throws RemotingException方法
    channel.send(res);

    NettyChannel类void send(Object message, boolean sent) throws RemotingException方法
    channel.send(message, sent);

    AbstractChannel类ChannelFuture writeAndFlush(Object msg)方法
    ChannelFuture future = channel.writeAndFlush(message);















	       