# simple-quartz-tasks
基于quartz的任务调度插件，引入到spring项目中实现任务信息装载接口即可使用，依赖于redis的订阅完成对任务的立即运行，暂停等操作。适合快速搭建任务服务器。
# 介绍
  * 此插件已经在我们线上服务器上从16年到目前都一直稳定运行，对项目中需要进行任务调度的或者需要单独的任务调度服务器的都可以使用此插件。
  * 本项目没有那么多强大的功能，结合redis可支持集群部署，任务不会被执行多次，旨在给需要简单的定时任务的项目提供便捷，提高开发者的开发效率，方便开发者快速完成任务进行交付。
  * 任务调度管理界面需要开发这自己实现，给服务器发送任务调度的指令请参考《设计说明.doc》文档
  * tasks-server-example是web例子，给大家提供参考，内有数据库初始化脚本
  * 有任何问题请call zhangyinhao1234@163.com
# 如何使用
  * 主要接口：ScheduleJobsOperate 实现此接口可以进行定时任务信息的加载，发送错误邮件，保存执行日志，获取任务信息等操作。此接口需要开发者实现。
  * 添加任务：在spring的配置文件中添加需要进行自动调度的任务，并且继承 AbstractJob 对抽象方法进行实现
  * redis的订阅发布请参考测试例子中，有详细的配置
  * 完成以上工作就可以开始启动你的项目了

# 依赖jar包
```xml
<dependency>
    <groupId>com.github.zhangyinhao1234.plugin</groupId>
    <artifactId>tasks-plugin</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Spring Boot快速集成

切换到分支2.0 查看进行快速集成