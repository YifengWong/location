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


## 3. C++ 入口