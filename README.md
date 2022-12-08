## paddi-im-server
基于SpringBoot+Mybatis-Plus+MapStruct+MySQL+Redis+Netty+WebSocket+MinIO+Nginx的即时通讯应用<br/>
该项目主要是后端部分，功能还在实现中。
### 完成的功能列表
- 私聊
- 文件互传
- 五子棋小游戏
### 待完成的功能列表
- 群聊
- 语音聊天
- 将消息发送和持久化进行解耦，并且多条消息进行异步发送通过时间戳来保证消息有序
- 类似TCP通过消息发送和应答ACK保证消息发送的可靠性
- 消息重发机制
- 消息队列和时间戳保证消息能够可靠的发送以及过滤网络原因而延迟的过期消息
