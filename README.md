# NetworkStudy
网络知识、网络框架以及相关技术的学习笔记

# BIO 架构模式
## ServerSocketTest01
> 在本项目中，演示了一个服务端到客户端的点到点的连接。

服务端与客户端双方，分别采用`InputStream` `DataInputStream` 与 `OutputStream` `DataOutputStream`。

为了增强复用性，这里使用线程池：
```java
//创建一个线程池 来负责线程的复用 如果先前有可用的线程则复用
            //否则创建新的 这里只需要关注 Runnable 实例
            ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
            newCachedThreadPool.execute(() -> {
                handlerCommunication(currentSocket);
            });
```

本例子只能支持一次性的通信。

## ServerSocketTest02
本例子中的亮点是，利用try-resources代替之前的try-catch。

```java
        //指定未被占用的端口号 创建一个服务器套接字
        try (ServerSocket serverSocket = new ServerSocket(8888);
             Socket currentSocket = serverSocket.accept();//监听客户端
             DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(currentSocket.getOutputStream())) {

            //在读数据之前 需要用到 标准的数据输入流来包装
            String message = dis.readUTF();
            System.out.println("[服务端] 收到消息，消息内容是：" + message);
            dataOutputStream.writeUTF("消息已经收到，这里是服务端！");
            dataOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
```

## ServerSocketTest03
```java
/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 * <p>
 * 并且服务端与客户端之间 可以一直收发消息
 * </p>
 *
 *
 * <p>
 * 注意，现在这个版本的程序还是有问题的。为什么？
 * 可以从while的轮询中 看出
 * 对于服务端而言，必须是 读-写 的操作。
 * 在轮询读的过程中，整个程序是处于阻塞状态的！因此，没有读到数据之前，不可以进行写操作
 * <p>
 *
 * <p>
 * 同理而言 客户端也是如此 不能反复读 或者反复写。
 * </p>
 */
```

如何解决这个问题呢？应该，将读与写相分离。

## ServerSocketTest04

04是在05的基础上，增加了群聊系统。核心点在于，服务端在收到A的消息后，会分别发给自己所管辖的其他各个客户端。

```java
/**
 * 创建服务器套接字 ServerSocket
 * 并且实现，在服务端套接字 接收到客户端套接字信息后 反发消息回去
 *
 * <p>
 * 并且可以实现群聊 即一个服务端 多个客户端
 * 怎么实现多个客户端都能被一个服务端监听到呢？
 * 将监听accept放在 while循环中
 * </p>
 *
 * <p>
 * 而且如何做到让某一个客户端发的信息 变成群聊呢？
 * 那就是： 比如客户端 c1 c2 c3都连上了服务端
 * 倘若c1向服务端发送消息 则服务端在接收到消息后
 * 又发给其他已经监听到的客户端
 * </p>
 *
 * <p>
 * 注意这个操作是要放在while中的 每次有人上线 则开一个线程
 * 专门负责消息的转发：读取 + 转发
 * </p>
 */
```

本案例中，实现了用线程封装读操作与写操作。

```java
new OutputClient(currentSocket).start();

new InputClient(currentSocket, "客户端").start();
```

怎么实现转发给所有客户端的呢？
```java
while (true) {
	try {
		//首先收到消息 然后发给所有人
		DataInputStream dis = new DataInputStream(currentSocket.getInputStream());
		String message = dis.readUTF();

		SocketApp.socketList.forEach(socket -> {
			try {
				DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
				dataOutputStream.writeUTF(message);
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	} catch (IOException e) {
		e.printStackTrace();
	}
}
```

## ServerSocketTest05 
其中是在03的基础上，使用线程封装，注意不能将线程的启动放在while循环的下方。那样的代码块是不可达的。

## TCPFileUplad
> 这个案例实现了TCP的上传功能。

详细一点说，就是客户端首先连接服务端。

连接之后，客户端将来源的文件，这个文件可以是本地存储的文件，也可以是网络中的文件。

转换为输入流，然后利用这个连接套接字自己的输出流，将输入流中的内容（即文件）进行写。

然后，服务端这边，通过这个相同的套接字的输出流。能够原封不动，将文件输出到本地路径中。

注意，这里还是使用的BIO。在没有监听到客户端之前，会一直阻塞在accept方法这里。

## UdpFileDownload
> 在这个案例中，实现了下载功能。

其实本质上还是IO的读写，只不过来源变成了指定URL上的HttpURLConnection连接。

从这个连接中，去读取并写入到本地。

## BasicBufferDemo
> 这个案例中，分别介绍了NIO中的核心组件之一：Buffer以及MappedBuffer与多Buffer的读与写。


注意Buffer的子类等，而且可以实现同一个Buffer的读写状态转换：flip() 转换成读状态。

所以一般的流程：**初始化，put放数据，flip转换为读状态，然后使用get读数据。**

MappedByteBuffer 可以让文件在内存（堆外内存）直接修改，操作系统不需要拷贝一次。案例中，直接对txt文本文件进行读写。

```java
/**
 * 所谓 Scattering 就是将数据写入 Buffer 时，可以采用 Buffer 数组，依次写入
 * 所谓 Gathering 就是从 buffer 中读取数据时 可以采用 buffer 数组 依次读出
 */
```

## BasicChannelDemo
> 注意，这里引入了NIO中的核心组件之一：Channel通道。

与ByteBuffer一起工作时，始终记住。**ByteBuffer是直接与数据打交道的，** 以字符串数据为例。

然后Channel通道是与Buffer打交道的。

字符串.getBytes()后得到字节数组，然后放入ByteBuffer中去。

而且Channel是通过文件输出流的getChannel()方法获得的，对Channel操作，就是对这个文件输出流操作。

而且，Channel另一头与Buffer进行联系，通过Read方法或者Write方法。


### 顺序问题
> 这里以将字符串数据写到文本文件中为例子：

1. 首先将字符串数据，放入Buffer中去。

2. 通道是通过文件输出流获得的对象。

3. 通道与Buffer进行交互，通过Write或者Read方法。


> 如果是从文件中读取呢？

1. 仍然是通过文件输入流，获得对应的通道。FileChannel

2. 这个FileChannel，把数据读到Buffer中去。fileChannel.read(buffer);

3. 利用Buffer.array() 方法转换为字节数组。

4. 利用new String(buffer.array()) 得到字符串数据

### FileChannelDemo03

这里主要是解决一个问题。在01与02的写与读中，我们会发现，分别都用了一个Buffer。也就是总共用了两个Buffer。

而这里，想实现一个功能。有输出流与对应的FileChannel，输入流与对应的FileChannel。

但是只用一个ByteBuffer，如何通过这个ByteBuffer的状态更改。实现，将a文本文件中的内容，写到b文本文件中去。

肯定是读，然后写。

核心代码是：
```java
//利用buffer 轮询读取源文件demo03.txt
while (true) {
	//清理缓存 准备写到缓存区中
	byteBuffer.clear();

	int read = fileInputChannel.read(byteBuffer);
	if (read == -1) {
		break;
	}

	//转换成从buffer中读取数据的状态
	byteBuffer.flip();

	//开始写
	fileOutputChannel.write(byteBuffer);
}
```

### FileChannelDemo04
> 这个案例中，主要实现通道与通道之间的文件拷贝。通过一个transferFrom的方法。

```java
//使用transferFrom完成拷贝 从input通道拷贝到output通道中
fileOutputChannel.transferFrom(fileInputChannel, 0, fileInputChannel.size());
```

这里目标文件的Channel自己的transferFrom方法，参数是源文件的Channel。


## 最重要的NIO群聊系统
> 在本次案例中，我们将吸取BIO架构的教训。并引入NIO的三大核心组件：Selector，Channel以及Buffer。做一个群聊系统，支持转发消息。

```java
/**
 * 编写一个NIO群聊系统 实现服务端与客户端之间 简单非阻塞的数据通讯
 *
 * <p>
 * 1. 实现多人群聊
 * 2. 服务端可以检测用户的上线与离线行为 并实现消息转发功能
 * 3. 客户端通过channel 可以非阻塞地发送消息给其他用户
 * 同时也能接收其他用户发送的消息（当然这个需要通过服务端转发给客户端）
 * </p>
 */
```

⭐⭐ 看完代码后，有几个比较重要的核心点：
1. 对于服务端来说，要完成重要的初始化工作。注意，channel.socket()可以获得ServerSocket。
	这些初始化工作，包括选择器，包括ServerSocket绑定端口，包括非阻塞模式以及将该Channel注册到选择器上，因为是服务端，所以是Accept事件。

2. 因为Netty是基于事件驱动的，事件有几个：accept表示准备好连接，read表示读取数据，write表示写事件，connect表示连接上了。
	客户端一方面要写消息给服务端，另一方面还要读取服务端的消息。因此这里选择的是，开启一个线程每隔三秒钟去读取服务端的数据，同时不间断地写消息给服务端。
	
3. 服务端有一个Selector有一个ServerSocketChannel，而客户端也有一个Selector和一个SocketChannel。

4. 如果要获得正在发生事件的Key集合，用selector.selectedKeys()方法。如果要找注册到该Selector的所有Channel，则用selector.keys()方法。

5. 每次利用SelectionKey集合中的iterator迭代器，使用了while(iterator.hasNext())时，都要remove掉，避免操作重复。

6. 客户端离线后，注意不再往事件集合中注册这个key，并且通道需要关闭。
