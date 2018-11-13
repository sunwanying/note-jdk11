# blog 大家一起做一个云笔记，学习 Spring Boot 技术

spring Boot 开始一个 blog 项目

## 项目管理通过 Gradle 来进行项目管理

## 整体采用 Spring Boot 框架，单体应用

* 验证和授权通过 JWTs 协议来做
* 数据库持久层采用的 MyBatis 框架
* 前端模板引擎采用的 thymeleaf

## 目录结构

### config
    主要存放整个项目的配置类
### controller
    前端的所有的 url 映射到对应的控制器
### domain
    所有数据库表字段映射的实体类
### mapper
    mybatis 的 mapper 类，对应于相应的 mapper.xml 文件
### security
    验证和授权相关的功能
### service
    业务具体实现
### utils
    工具类
    
## 编译

`./gradlew build --refresh-dependencies -x test`

`nohup java -jar $JOB_NAME.jar --spring.profiles.active=prod &`