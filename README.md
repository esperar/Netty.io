# Netty.io

Netty 프레임워크를 기술한 노트입니다. ✍️

핵심 개념들을 마크다운(.md) 파일로, 실전 예제는 Java를 사용해 작성합니다. ✨

작성된 내용중 잘못된 정보나 추가해야할 정보들은 이슈로 제보해주시면 감사하겠습니다. 🤝


## Netty?🤝

> Netty는 Java 언어로 작성된 이벤트 기반 네트워크 애플리케이션 프레임워크입니다. Netty를 사용하면 고성능, 확장 가능하고 유연한 서버 및 클라이언트 애플리케이션을 개발할 수 있습니다. Netty는 다양한 프로토콜 (HTTP, WebSocket, TCP 등)을 지원하며, 비동기 및 논블로킹 I/O 모델을 사용하여 네트워크 통신을 처리합니다. Netty는 대규모 분산 시스템에서 많이 사용됩니다. Netty를 사용하는 기업으로는 Facebook, Twitter, Apple, Spotify, Netflix 등이 있습니다.

### Dependency

#### maven
```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.68.Final</version>
</dependency>

```

#### gradle

```gradle
implementation 'io.netty:netty-all:4.1.68.Final'
```

<br>

## TimeLine

- 자바의 네트워킹
  - [Java NIO](https://github.com/esperar/netty.io/blob/master/1-1%20NIO/nio.md)
  - [Selector](https://github.com/esperar/netty.io/blob/master/1-1%20NIO/selector.md)
- 네티 소개
  - [~~네티는 누가 사용할 까?~~]()
  - [비동기식 이벤트 기반 네트워킹](https://github.com/esperar/netty.io/blob/master/1-2%20Intro/async.md)
- 네티의 핵심 컴포넌트
  - [Channel](https://github.com/esperar/netty.io/blob/master/1-3%20Component/netty_component.md)
  - [콜백](https://github.com/esperar/netty.io/blob/master/1-3%20Component/netty_component.md)
  - [Future](https://github.com/esperar/netty.io/blob/master/1-3%20Component/netty_component.md)
  - [이벤트와 핸들러](https://github.com/esperar/netty.io/blob/master/1-3%20Component/netty_component.md)
- 네티 애플리케이션 Echo 서버, 클라이언트 만들기
  - [Server](https://github.com/esperar/Netty.io/blob/master/2-netty-echo-sample/src/main/java/netty/nettyecho/server/EchoServer.java)
  - [Client](https://github.com/esperar/Netty.io/blob/master/2-netty-echo-sample/src/main/java/netty/nettyecho/client/EchoClient.java)
  - [+ @Sharable 어노테이션](https://github.com/esperar/Netty.io/blob/master/2-netty-echo-sample/sharable.md)
  - [+ SimpleChannelInboundHandler vs ChannelInboundHandler](https://github.com/esperar/Netty.io/blob/master/2-netty-echo-sample/sharable.md)

<br>

### Reference
- [Netty.docs Wiki](https://netty.io/wiki/)  
- [Reactor Netty Reference Guide](https://projectreactor.io/docs/netty/release/reference/index.html)