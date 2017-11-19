# 室内定位系统

## 1. 后台模块组成

#### 1.1 逻辑流程
1. 用户使用手机端App拍摄视频过程中，会间隔一定时间发送图片帧到Java Web http接口
2. Java Web应用接收图片并整理顺序保证时序无错乱后，将图片帧存放至本地Redis
服务中
3. C++部分代码提供封装好的接口，可以从本地Redis中读取图片，并提交给算法进行处理
4. 算法处理完毕后返回结果，存放在Redis服务中
5. Java Web可以从Redis中定时查询所需结果

#### 1.2 Java Web应用中使用技术
1. Spring MVC框架
2. Jedis 操作Redis
3. Spring框架


#### 1.3 C++ 入口封装使用技术
1. hiredis 操作Redis
2. C++ Socket


## 2. Java Web应用

#### 2.1 主要包 location.message内容
![](https://github.com/YifengWong/location/blob/master/docs/pics/java-message-package-classes.png)

#### 2.2 实体类 Message

1. 作为与C++沟通的主要消息实体，可以用于保持一个具体的消息。
2. 一个具体的消息分为多种，有图片类型，结果类型，可以与C++算法部分进行完备的交流。
3. 消息还可以保持前端发送过来的传感器数据，作为参数存放。
4. 有序列化与反序列化方法，作为内存对象写入与C++一致。

#### 2.3 AbstractMsgService以及子类不同实现RedisMsgService和SocketMsgService

1. 该部分的类主要用于与C++进行通信。有Redis方法以及Socket方法。
2. 抽象类实现了共同的方法，且保持有MsgManger对象，用于对消息的管理。
3. RedisMsgService实现了针对Redis通信方式的线程启动。
4. SocketMsgService实现了针对Socket通信方式的线程启动。


#### 2.4 消息管理中心 MsgManager

1. 该类用于管理消息，作为Java端的消息中心，对外提供访问方法。
2. 无论是获取消息还是发送消息，都可以调用MsgManager的方法，由其进行统一处理，这样便于优化。


#### 2.5 其余内容

作为一个普通Web应用，使用Spring定义的Controller提供Web接口。


## 3. C++ 入口