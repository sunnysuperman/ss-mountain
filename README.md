# 框架一览
- 语言包: ss-mountain-lang
- 测试: ss-mountain-test
- 环境上下文: ss-mountain-base
- 异步多线程: ss-mountain-task
- HTTP客户端: ss-mountain-httpclient
- 校验: ss-mountain-validation
- 文档: ss-mountain-swagger2
- WEB: ss-mountain-web
- 数据库: ss-mountain-repository(ORM)、ss-mountain-db(ORM)、ss-mountain-transaction(事务)
- 分布式锁: ss-mountain-lock(接口)、ss-mountain-lock-redis(实现)
- 缓存: ss-mountain-cache(接口)、ss-mountain-cache-redis(实现)
- 消息列队: ss-mountain-mq(接口)、ss-mountain-mq-rocketmq4(实现)、ss-mountain-mq-rocketmq5(实现)
- 定时任务: ss-mountain-job(接口)、ss-mountain-job-xxl(实现)
- 可靠事件: ss-mountain-evt
- 本地事件: ss-mountain-localevent
- 全文搜索: ss-mountain-search
- 文件处理: ss-mountain-file
- 数据导出: ss-mountain-export
- 随机ID: ss-mountain-randomid

# 语言包(ss-mountain-lang)
- 对象: Obj
- 文本: Str
- 布尔: Bool
- 数字: Num
- 日期: Dates
- 集合: Colls
- 数组: Arrays
- 链接: Url
- 编码: Base62/Jsons
- IO操作: IOUtil
- 重试: Retryer
- 循环遍历: LoopExecutor/InterruptableLoopExecutor
- 分页:

```
1、Page/PullPage/PageRequest/PullPageRequest等基础类
2、PageQuery/PullPageQuery等包装类
4、PageHelper/PullPageHelper/MarkerUtils等实用类
```
- 模型: DataHolder(数据持有)/BoolHolder(布尔数据持有)/Options(查询对象选项)/SameAware(对象比较)/ApiResult(API输出)
- 异常: UnexpectedException/FormatException/FatalException/ServiceExceptions.wrap/Exceptions.wrapRuntimeException
- 进程类: ProcessUtils
- 等待通知机制: BooleanLock/Flag
- 不依赖数据库ID: ObjectId/ObjectIdGeneratorFactory

# 测试(ss-mountain-test)
- 测试类: 继承Tester
- 断言: assertTrue/assertFalse/assertEquals等
- 打印: print
- 交互式: prompt
- 异步: waitUntil/waitAMoment
- 分页: newPageQuery/newPullPageQuery
- 文件: newFile/downloadFile/openFile

# 校验(ss-mountain-validation)
- 注解: 分为4类

```
1、javax.validation注解(@Valid、@NotNull、@NotEmpty等)
2、ss-validation-api注解(@EmptyToNull、@Length等)
3、自定义注解（自定义校验）
4、Validated接口（自定义校验）
```
- 自定义注解: 需要实现自定义校验器CustomValidator

# 环境上下文(ss-mountain-base)
- 包扫描配置: ss-mountain.base.scan-packages
- 组件管理: ComponentManager(同时解决循环依赖)
- 环境管理: EnvHelper

# 异步多线程(ss-mountain-task)
- TaskHelper.addTask/scheduleTask/runInParallel
- 事件(evt)/缓存(cache)默认使用TaskHelper的线程池，可自定义线程池

# 文档(ss-mountain-swagger2)
- 配置: ss-mountain.swagger2.enabled

# WEB(ss-mountain-web)
- 异常拦截: DefaultExceptionResolver
- 默认拦截器: DefaultInterceptor(需要被继承)，上下文/跨域处理/屏蔽测试接口

# 数据库(ss-mountain-db)
- 核心接口类: CRUDRepository、PrimaryDBRepository
- 实用类: SqlBuilder

# 缓存(ss-mountain-cache)
- 引用: ss-mountain-cache(接口)、ss-mountain-cache-redis(实现)
- 核心接口类: CacheFactory

# 消息列队(ss-mountain-mq)
- 引用: ss-mountain-mq(接口)、ss-mountain-mq-rocketmq4(实现)、ss-mountain-mq-rocketmq5(实现)
- 核心接口类: MQProducer、@MessageListener

# 定时任务(ss-mountain-job)
- 引用: ss-mountain-job(接口)、ss-mountain-job-xxl(实现)
- 核心接口类: JobExecutor

# 可靠事件(ss-mountain-evt)
- 核心接口类: EvtPublisher、@EvtListener

# 本地事件(ss-mountain-localevent)
- 核心接口类：LocalEventPublisher、LocalEventSubscriberManager

# 全文搜索(ss-mountain-search)
- 核心接口类：SearchHelper

# 文件处理(ss-mountain-file)
- 核心接口类：FileUtil/FileHelper/FileDownloader

# 数据导出(ss-mountain-export)
- 核心接口类：GenericExportService、ExportJob、ExportJobRepository、ExportJobExecutor

# 随机ID(ss-mountain-randomid)
- 引用：ss-mountain-randomid、定时任务实现库(如ss-mountain-job-xxl)
- 核心接口类：RandomIdGeneratorFactory
