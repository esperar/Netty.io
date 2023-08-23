# ChannelPipeline 인터페이스

ChannelPipeline은 ChannelHandler 체인을 위한 컨테이너를 제공하며, 체인 상에서 인바운드와 아웃바운드 이벤트를 전파하는 API를 제공한다.

Channel이 생성되면 여기에 자동으로 자체적인 ChannelPipeline이 할당된다.

ChannelHandler는 다음과 같이 ChannelPipeline안에 설치된다.

1. ChannelInitializer 구현체를 ServerBootstrap에 등록한다.
2. ChannelInitalizer.initChannel()이 호출되면 ChannelInitializer가 ChannelHandler의 커스텀 집합을 파이프라인에 설치한다.
3. ChannelInitalizer는 ChannelPipeline에서 자신을 제거한다.

<br>

## 예시

```java
EventLoopGroup workerGroup = new NioEventLoopGroup(); // NIO 이벤트 루프 그룹 생성
Bootstrap bootstrap = new Bootstrap(); // 부트스트랩 객체 생성
bootstrap.group(workerGroup) // 부트스트랩에 이벤트 루프 그룹 지정
         .channel(NioSocketChannel.class) // 채널 유형 설정
         .handler(new ChannelInitializer<SocketChannel>() { // 클라이언트 채널 초기화
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline pipeline = ch.pipeline(); // 채널 파이프라인 객체 생성
                 pipeline.addLast("decoder", new StringDecoder()); // 문자열 디코더 추가
                 pipeline.addLast("encoder", new StringEncoder()); // 문자열 인코더 추가
                 pipeline.addLast("handler", new MyClientHandler()); // 클라이언트 핸들러 추가
             }
         });
```

ChannelHandler는 광범위한 용도로 지원할 수 있게 설계되었으며, ChannlPipeline을 통해 오가는 이벤트를 처리하는 모든 코드를 위한 범용 컨테이너라고 할 수 있다.

핵심 하위 인터페이스로는 ChannelInboundHandler, ChannelOutboundHandler가 있다.

파이프라인을 통해 이벤트를 이동하는 역할은 Application의 부트스트랩 단계나 초기화 중에 설치된 ChannelHandler가 담당한다.

이들 객체는 이벤트를 수신하고, 구현된 처리 논리를 실행하며, 체인 상의 다음 ChannelHandler로 데이터를 전달한다.

**실행되는 순서는 추가된 순서에 의해 결정된다. ChannelPipeline이라고 말할 때는 이러한 ChannelHandler의 정렬된 배치 전체를 의미한다고 보면 된다.**

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmJN4m%2FbtqEbPOcIg7%2F5G3AvkOekAqQF6L4faqxk0%2Fimg.png)

아웃바운드와 인바운드 작업이 서로 다르다는 것을 감안할 때, 동일한 ChannelPipeline 안에 두 가지 핸들러 범주가 혼합 돼 있으면 Netty는 ChannelInboundHandler, ChannelOutboundHandler의 구현을 구분하며, 핸들러 간의 데이터 전달이 동일한 방향으로 수행되도록 보장한다.

ChannelHandler를 ChannelPipeline에 추가할 때 ChannelHandler 및 ChannelPipeline 간의 바인딩을 나타내는 ChannelHandlerContext 하나가 할당된다.

이 객체는 기본 Channel을 가져오는 데 이용할 수 있지만, 실제로는 아웃바운드 데이터를 기록할 때 주로 이용된다.

Netty에서 메시지를 보내는 방법은 두가지가 있다.
- Channel에 직접 기록한다.
- ChannelHander와 연결된 ChannelHandlerContext 객체에 기록한다.

전자는 ChannelPipeline에 끝단에서 시작되며, 후자는 메시지를 기록한 핸들러의 다음 핸들러에서 시작된다.

**인코더와 디코더도 ChannelHandler 인터페이스의 구현체이며 Binary 데이터를 Application에서 사용할 포맷에 맞게 변환하거나 Application의 포맷을 Binary 데이터로 변환하여 출력하는 로직에 사용된다,**
