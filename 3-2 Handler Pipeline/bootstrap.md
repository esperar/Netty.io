# 부트스트랩

네티의 부트스트랩 클래슨느 프로세스를 지정된 포트로 바인딩(서버 푸트스트랩)하거나 프로세스를 지정된 호스트의 지정된 포트에서 실행중인 다른 호스트로 연결(클라이언트 부트스트랩)하는등의 일을 하는 애플리케이션의 네트워크 레이어를 구성하는 컨테이너를 제공한다.

즉 Bootstrap이란 Application의 동작 및 설정을 지정해주는 helper class이다.

사전적 의미를 찾아보면 "그 자체의 동작에 의해서 어떤 소정의 상태로 이행하도록 설정되어 있는 방법"이다.

Bootstrap을 통해 네티의 Socket mode, thread 등을 쉽게 설정할 수 있다.

그리고 EventHandler 또한 Bootstrap을 통해 설정해야한다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcViimq%2FbtqVCcZxHra%2FrtHQHkvFuv6kcWvf77Uogk%2Fimg.png)

<br>

## 예제

서버 푸트스트랩은 ServerBootstrap을 이용하고 클라이언트 부트스트랩은 Bootstrap을 이용한다.

```java
@RequiredArgsConstructor 
public EchoServerEx(){ 
  private final ServerBootstrap bootstrap; 
  
  public static void main(String[] args) { 
    EventLoopGroup parentGroup = new NioEventLoopGroup(1);
    EventLoopGroup childGroup = new NioEventLoopGroup(); 
    bootstrap.group(bossGroup, childGroup) 
    .channel(NioServerSocketChannel.class) 
    .childHandler(new ChannelInitializer<SocketChannel>() { 
      @Override
      protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline(); 
        pipeline.addLast(new EchoServerHandler()); 
      } 
    }); 
  } 
  bootstrap.bind(host, port).sync(); 
}
```


![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbfIDsc%2FbtqQIVO6Zks%2F2PKqbbxAtTi95YV35i4eV1%2Fimg.jpg)

**클라이언트를 부트스트랩할 때는 EventLoopGroup 하나가 필요하지만 서버는 두 개가 필요하다.**

서버는 각기 다른 Channel의 두 집합을 필요로 한다.

첫 번째 집합은 **로컬 포트로 바인딩된 서버 자체**의 소켓을 나타내는 ServerChannel

두 번째 집합은 서버가 수락한 연결마다 하나씩 들어오는 클라이언트 연결을 처리하기 위해 생성된 모든 Channel

<br>

## Bootstrap API

### group
- 이벤트 루프에 대한 설정
- parentGroup은 Client의 connection 요청을 수락하는 역할을 담당하고
- childGroup은 I/O와 이벤트 처리를 담당한다.

### channel
- 소켓 입출력 모드 설정.

```
- NioServerSocketChannel: Nonblocking 소켓 채널 
- OioServerSocketChannel: Blocking 소켓 채널 
- LocalServerChannel: 로컬 가상 통신을 위한 소켓 채널 
- EpollServerSocketChannel: epoll 소켓 채널 
- OioSctpServerChannel: Blocking sctp 소켓 채널 
- NioSctpServerChannel: Nonblocking sctp 소켓 채널 
- NioUdtByteAcceptiorChannel: Nonblocking udt 소켓 채널 
- NioUdtMessageAcceptorChannel: blocking udt 소켓 채널
```

### channelFactory
- channel 처럼 소켓 입출력 모드를 설정하는데 기본적으로 제공하는 channel class 보다 더 복잡한 로직이 필요할 때 사용한다.

### handler
- server socket channel의 event handler 설정 부모 스레드에서 발생한 이벤트(connect, close..)만 처리

### childHandler
- client socket channel의 event handler 설정 자식 스레드에서 발생한 이벤트(read, write..)만 처리

### option
- server socket channel의 socket option 설정

```
TCP_NODELAY: Nagle 알고리즘 비활성화 여부 설정 
SO_KEEPALIVE: 정해진 시간마다 keepalive packet 전송 
SO_SNDBUF: 커널 송신 버퍼 크기 
SO_RCVBUF: 커널 수신 버퍼 크기 
SO_REUSEADDR: TIME_WAIT 상태의 포트에도 bind 가능해짐 
SO_LINGER: 소켓을 닫을 때 송신 버퍼에 남은 데이터 전송 대기
```

### childOption
- client socket channel의 socket option 설정

