# 伙伴交友中心

伙伴交友中心，一个前后端分离的用户匹配系统，前端使用**Vite + Vue3 + Vant4**，后端使用**SpringBoot** + **Mybatis-plus**，使用 **WebSocket**实现实时通信。本项目使用Apache License Version 2.0开源协议。

## 图片预览

![](https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/1702393460827-0ff9f140-445a-4860-b4fc-70a404aa7339.png)

![](https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/1702393465235-0da600e0-eeff-48cb-9422-ec2f4759f989.png)

![](https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/1702393471267-c926ccb3-d6fe-41b2-a083-99a44969dcb9.png)

![image.png](https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/1702393432962-3380a91b-02bb-4b49-8599-42a870e4bf44.png)

![image.png](https://image-bed-ichensw.oss-cn-hangzhou.aliyuncs.com/1702393439216-da91a0f6-88b8-4aba-95b8-fa04656ae863.png)

## 项目意义

帮助同学在学习的道路上找到志同道合的伙伴！！！

## 核心功能

1. 用户注册和登录：用户可以通过注册账号并登录使用该网站。
2. 标签匹配：用户可以选择自己的技能和需求标签，系统会根据标签匹配合适的队友。
3. 组队功能：用户可以与其他用户组建队伍，一起参加编程比赛。
4. 实时聊天：队伍中的用户可以进行实时聊天，方便沟通和协作。
5. 用户管理：管理员可以对用户进行管理，包括审核用户信息和处理用户投诉等。

## 项目亮点

1. 用户登录：使用 Redis 实现分布式 Session，解决集群间登录态同步问题；使用token储存用户信息并实现续签和超时自动退出。
2. 对于项目中复杂的集合处理（比如为队伍列表关联已加入队伍的用户），使用 Java 8 Stream API 和 Lambda 表达式来简化编码。
3. 使用 Redis 缓存首页高频访问的用户信息列表，将接口响应时长从 12520ms缩短至400ms。且通过自定义 Redis 序列化器来解决数据乱码、空间浪费的问题。
4. 为解决首次访问系统的用户主页加载过慢的问题，使用 quartz 定时任务来实现缓存预热，并通过分布式锁保证多机部署时定时任务不会重复执行。
5. 为解决同一用户重复加入队伍、入队人数超限的问题，使用 Redisson 分布式锁来实现操作互斥，保证了接口幂等性。
6. 使用 Knife4j + Swagger 自动生成后端接口文档，并通过编写 ApiOperation 等注解补充接口注释，避免了人工编写维护文档的麻烦。
7. 使用本地+云服务储存用户头像，并自定义cdn加速域名指向项目专用储存空间。
8. 使用WebSocket在单个TCP连接上进行全双工通信，创建持久性的连接，实现队伍聊天室中的实时聊天。
9. 前端使用 Vant UI 组件库，并封装了全局通用的 Layout 组件，使主页、搜索页、组队页布局一致、并减少重复代码。

## 技术选型

**前端**

- Vue 3
- Vite 脚手架
- Vant UI 移动端组件库
- Axios 请求库

**后端**

- JAVA SpringBoot 框架
- MySQL 数据库
- Mybatis-Plus
- Mybatis X
- Redis缓存
- Redisson 分布式锁
- Swagger + Knife4j 接口文档
- Gson JSON序列化库
- WebSocket

## 前端部署

1. 启动cmd并进入项目所在文件夹
2. 执行npm install
3. 执行npm run dev
4. 访问 [http://localhost:5173](https://gitee.com/link?target=http%3A%2F%2Flocalhost%3A5173)

