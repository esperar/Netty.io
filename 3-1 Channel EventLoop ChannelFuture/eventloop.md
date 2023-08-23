# EventLoop 인터페이스

EventLoop는 **연결의 수명주기 중 발생하는 이벤트를 처리하는 네티의 핵심 추상화를 정의**한다.

다음 그림은 Channel, EventLoop, EventLoopGroup 간의 관계를 개략적으로 보여준다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FHwKiE%2FbtqQE9ArMgQ%2FfWRfQ9RZ0mvqkY8DrElkG1%2Fimg.jpg)

- `EventLoopGroup`은 하나 이상의 `EventLoop`를 포함

- `EventLoopGroup`은 `Channel`에 `EventLoop`를 할당
- `EventLoop`는 수명주기 동안 하나의 `Thread`로 바인딩
- `EventLoop`에서 처리되는 모든 입출력 이벤트는 해당 전용 `Thread`에서 처리
- 하나의 `Channel`은 수명주기 동안 하나의 `EventLoop`에 등록 가능
- `EventLoop`를 하나 이상의 `Channel`로 할당할 수 있다.

<br>

## 멀티 플렉싱

EventLoop는 이름처럼 계속해서 루프를 돌며 이벤트를 처리하는 네티의 핵심 구성 요소 중 하나다.

이벤트 루프는 **멀티플렉싱(multiplexing)** 방식을 사용해 하나의 쓰레드로 여러 채널에서 발생하는 이벤트를 병행해서 치라가 가능하다.

이벤트 루프가 처리하는 이벤트의 종류는 크게 두 종류로 나눌 수 있다.
1. 저수준 채널에서 발생하는 I/O 이벤트
2. 사용자의 I/O 처리 요청

예를 들면 이벤트 루프는 네트워크 채널에서 읽기 가능한 데이터가 수신되면 데이터를 읽어서 처리하기도 하고 사용자가 데이터 쓰기 요청을 하는 경우 이를 받아 채널로 전송하기도 한다.

```java
// 이벤트 루프 공유
@Test
void oneEventLoopHandleMultiChannels() throws Exception {
    // When: 2개의 클라이언트 연결 (1개 이벤트 루프 공유)
    int nThread = 1;
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup(nThread);
    clientOne = newConnection("localhost", 12345, eventLoopGroup);
    clientTwo = newConnection("localhost", 12345, eventLoopGroup);

    // Then: 하나의 이벤트 루프 공유하여 정상 통신
    assertSame(clientOne.test().eventLoop(), clientTwo.test().eventLoop());
    assertNormalCommunication();
}

// 서버와 클라이언트 간 일반적인 데이터 교환 테스트
void assertNormalCommunication() throws Exception {
    // 서버의 통신 가능 상태 대기
    await().until(() -> server.isActive(clientOne.localAddress()) &&
                        server.isActive(clientTwo.localAddress()));

    // 서버 -> 클라이언트
    server.sendAll("Hello All");
    assertEquals("Hello All", clientOne.read(1, TimeUnit.SECONDS));
    assertEquals("Hello All", clientTwo.read(1, TimeUnit.SECONDS));

    // 클라이언트 -> 서버
    clientOne.send("Hello I am One");
    assertEquals("Hello I am One", server.read(clientOne.localAddress(), 1, TimeUnit.SECONDS));
    clientTwo.send("Hello I am Two");
    assertEquals("Hello I am Two", server.read(clientTwo.localAddress(), 1, TimeUnit.SECONDS));
}
```

<br>

## 그룹핑

이벤트 루프는 이벤트 그룹이라는 상위 모듈에 의해 그룹핑이 된다.

이벤트 루프 그룹이 생성될 대 기본적으로 cpu 코어 x 2 개의 이벤트 루프를 생성하고 관리한다.

물론 생성되는 이벤트 루프 개수를 변경할 수도 있다.

이러한 그룹핑을 통해 추후 연관된 이벤트 루프들에서 관리하는 채널들을 한 번에 간단히 종료할 수도 있다.

서버 애플리케이션이라면 아래와 같은 코드 한 줄을 통해 서버에 접속된 모든 클라이언트들과 연결을 닫고 이벤트 루프를 종료시켜 줄 수 있다.

```java
// 서버에 접속된 모든 클라이언트 연결 종료
bootstrap.config().childGroup().shutdownGracefully();
```

<br>

## 채널에 할당되는 이벤트 루프

이벤트 루프 그룹은 부트스트랩(BootStrap)을 통해 연결이 수립되고 통신 가능한 채널(Channel)이 생성되기 위한 필수 요소다.

따라서 초기화 과정에 이벤트 루프 그룹이 부트스트랩에 할당되어야 한다.

> 부트스트랩: 컴퓨터 시스템이 부팅될 때 실행되는 초기화 코드, 이벤트 루프 그룹을 생성하고, 채널을 초기화하며, 채널 파이프라인을 설정해준다.

이제 부트 스트랩으려 연결을 시도하면 이벤트 루프 그룹에서 관리하는 이벤트 루프 하나가 선택되어 생성하는 채널에 할당된다.

이벤트 루프 선태겡는 단순하게 라운드 로빈 방식이 사용된다.

<br>

## 이벤트 루프 전용 쓰레드

이벤트 루프는 자신의 독립적인 쓰레드를 가집니다.

그러나 이벤트 루프가 생성될 때 쓰레드를 생성하지는 않습니다.

이벤트 루프의 전용 쓰레드는 사용자가 부트스트랩으로 연결을 요청할 때 생성되고 시작됩니다.

한 번 시작된 이벤트 루프 쓰레드는 종료 요청이 있을 때까지 계속해서 살아있게 됩니다.

심지어 처음에 자신을 생성되게 한 채널 연결이 종료되더라고 이벤트 루프 쓰레드는 이후 다른 채널을 서비스하기 위해 살아서 대기합니다.

따라서 처음에 N번의 연결 요청이 있을 때 까지만 쓰레드가 생성되고 이후 부터는 쓰레드풀 처럼 미리 생성된 쓰레드가 재사용됩니다.

이벤트 루프는 쓰레드 풀처럼 운영체제의 고비용 지원 중 하나인 쓰레드를 최대한 효율적으로 사용하려는 목적을 가집니다.

```java
@Test
void keepAliveEvenIfChannelGetClosed() throws Exception {
    // Given: 연결된 채널의 이벤트 루프 쓰레드 획득
    EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup(1);
    clientOne = newConnection("localhost", 12345, clientEventLoopGroup);
    Thread threadOne = clientOne.test().eventLoopThread();

    // When: 연결 끊기
    clientOne.disconnect();
    await().atMost(1000, TimeUnit.MILLISECONDS)
            .until(() -> !server.isActive(clientOne.localAddress()));

    // Then: 이벤트 루프의 쓰레드는 살아있음
    await().during(3000, TimeUnit.MILLISECONDS)
            .until(threadOne::isAlive);
}
```

<br>

## 불필요하게 생성되는 쓰레드 관리

이벤트 루프의 쓰레드는 연결이 요청될 때 생서오딘다고 앞서 설명했다.

정확하게 이해해야 하는 사실 한 가지가 있는데 그것은 이벤트 루프 쓰레드는 연결이 성공한 후에 생성되는 것이 아니라 연결을 시도하기 전에 생성된다는 사실이다.

따라서 다른 관점에서 연결이 실패한 경우에도 이벤트 루프 쓰레드가 생성되어 남아있게 된다.

만약 시스템이 대규모의 사용자 요청을 처리하는 시스템이 아니라 하나의 서버에 연결하는 하나의 클라이언트를 다루는 시스템이라면 연결이 실패할 때 마다 사용되지 않는 쓰레드 하나가 생성되는 상황이 생긴다.

따라서 다음과 같이 연결이 실패한 경우에는 생성된 이벤트 루프 쓰레드를 종료하도록 설정할 수도 있다.

```java
bootstrap.connect().addListener((CahnnelFutureListener) future -> {
    if(!future.isSuccess())
        future.channel().eventLoop().shutdownGracefully()
});
```


