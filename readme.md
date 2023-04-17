# 开放式聊天室（Open Chat Room）
## 这是什么
&emsp;&emsp;开放式聊天室是一款开源免费的聊天室框架，其设计目的是给予聊天机器人、
自然语言模型和远程聊天软件的开发人员更大的自由和更少的困难.

&emsp;&emsp;开放式聊天室通过Java SPI机制加载插件，但这并不意味着您一定需要使用
Java SPI机制来开发您的插件，事实上使用TCP网络协议仍旧可以方便的进行插件开发.
## 快速开始
    首先，确保 JDK 19 已被正确安装，
    推荐使用 JetBrains IntelIJ IDEA 作为 IDE 进行开发
您可能想要：
- ### 自行构建 Windows Client 以开始工作

    ```
    导入项目文件夹为 Maven 项目
    等待 Maven 解析完成所有依赖（应当包括 javafx-fxml、 javafx-controls 以及 lombok）
    使用 JDK 19 构建 Windows Client 项目，设置主类为 client.BootLauncher
    运行主类即可
    ```
- ### 构建适用于 Windows Client 的 Bot.jar插件

    ```
    复制 DemoBot 文件夹，并作为新的工程
    修改 DemoBot 类具体方法内容，或自行实现 toolkit.protocol.Friend
    在 src/main/resources/META-INF/services/toolkit.protocol.Friend 中注册您的实现类型
    构建Jar包，注意 open-chatroom-base 编译输出是不需要打包的
    首次运行 Windows Client 后，您应当发现存在一个 plugins 文件夹
    将您的Jar包放入该文件夹
    再次运行 Windows Client 后，您的Bot会被加载并作为一个新的可聊天对象出现于列表中
    ```
- ### 将 Windows Client 用于您的网络机器人交互

    ```
    首次运行 Windows Client 后，您应当发现存在一个 online friends.csv 文件
    修改这个文件，每行一个 TCP 终端，格式为
    IP, port, name
    其中 name 将作为 Bot 名称
    当成功连接至您的服务后，Bot 会作为一个新的可聊天对象出现于列表中
    ```
     
