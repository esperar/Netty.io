# Netty.io

Netty 프레임워크를 기술한 노트입니다. ✍️

마크다운(.md) 파일로 작성합니다. ✨

작성된 내용중 잘못된 정보나 추가해야할 정보들은 이슈로 제보해주시면 감사하겠습니다. 

## Netty?🤝
___

> Netty는 Java 언어로 작성된 이벤트 기반 네트워크 애플리케이션 프레임워크입니다. Netty를 사용하면 고성능, 확장 가능하고 유연한 서버 및 클라이언트 애플리케이션을 개발할 수 있습니다. Netty는 다양한 프로토콜 (HTTP, WebSocket, TCP 등)을 지원하며, 비동기 및 논블로킹 I/O 모델을 사용하여 네트워크 통신을 처리합니다. Netty는 대규모 분산 시스템에서 많이 사용됩니다.

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

- 1 자바의 네트워킹
  - [Java NIO](https://github.com/esperar/netty.io/blob/master/netty/1-1/nio.md)
  - [Selector](https://github.com/esperar/netty.io/blob/master/netty/1-1/selector.md)
- 2 네티 소개
  - [~~네티는 누가 사용할 까?~~]()
  - [비동기식 이벤트 기반 네트워킹](https://github.com/esperar/netty.io/blob/master/netty/1-2/async.md)
- 3 네티의 핵심 컴포넌트
  - [Channel](https://github.com/esperar/netty.io/blob/master/netty/1-3/netty_component.md)
  - [콜백](https://github.com/esperar/netty.io/blob/master/netty/1-3/netty_component.md)
  - [Future](https://github.com/esperar/netty.io/blob/master/netty/1-3/netty_component.md)
  - [이벤트와 핸들러](https://github.com/esperar/netty.io/blob/master/netty/1-3/netty_component.md)
