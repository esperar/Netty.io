# Channel 인터페이스

### 개요

이번 장에서는 네티의 네트워킹 추상화를 대표한다고 말할 수 있는 Channel, EventLoop, ChannelFuture 클래스를 더 자세히 알아보겠다.

이 세 가지 개념의 기능들을 다음과 같이 요약해볼 수 있다.

- Channel: Socket
- EventLoop: 제어 흐름, 멀티 스레딩, 동시성 제어
- ChannelFuture: 비동기 알림

이번장에서는 그 중 Channel 인터페이스를 알아보겠다.

<br>

## Channel

자바 기반 네트워크에서 기본 구조는 Socket 클래스다.

네티의 Channel 인터페이스는 Socket으로 직접 작업할 때의 복잡성을 크게 완화하는 API를 제공한다.


### 기능
Channel은 아래의 기능을 제공한다.
- 채널의 현재 상태(예: 열려있는가? 연결되어 있는가?)
- 채널의 구성 매개 변수(예: 수신 버퍼 크기)
- 채널이 지원하는 I/O 작업(예: 읽기, 쓰기, 연결 및 바인드)
- CHannelPipleline 채널과 관련된 모든 I/O 이벤트와 요청을 처리

### API


![](https://parkhyeokjin.github.io/img/netty/netty-channel.PNG)

클래스 | 기능
--|--
EmbeddedChannel | 실제 연결없이 ChannelHandlers의 테스트를 할 수 있도록 구현한 채널 클래스
EpollChannel | 최대 성능을 위해 EPOLL Edge-Triggered Mode를 사용하는 리눅스용으로 최적화된 채널 클래스
KQueueChannel | jni 라이브러리를 사용 하는 채널 클래스
LocalChannel | 로컬 채널 클래스
NioServerChannel | NIO 채널 클래스
SctpChannel | 멀티 스트리밍 및 멀티 호밍을 지원하는 메시지 지향 채널 클래스

<br>

## 예제

다음은 Netty Channel Interface를 구현한 MyChannelHandler 클래스다.

이 클래스는 클라이언트가 서버와 연겨로딜 때, 클라이언트로부터 메시지를 받았을 때, 예외가 발생했을 때의 동작을 정의한다.

```java
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

public class MyChannelHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("Client " + incomingChannel.remoteAddress() + " has connected.");
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("Received message from client " + incomingChannel.remoteAddress() + ": " + msg);
        
        // Echo back the received message to the client
        incomingChannel.writeAndFlush(msg);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incomingChannel = ctx.channel();
        System.out.println("Exception caught from client " + incomingChannel.remoteAddress() + ": " + cause.getMessage());
        
        // Close the channel
        ctx.close();
    }
    
}
```