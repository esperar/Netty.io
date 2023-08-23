# ChannelHandler 인터페이스

개발자의 관점에서 네티의 핵심 컴포넌트는 인바운드와 아웃바운드 데이터의 처리에 적용되는 모든 애플리케이션 논리의 컨테이너 역할을 하는 **ChannelHandler**이다.

이런게 가능한 이유는 ChannelHandler의 메서드가 네트워크 이벤트에 의해 트리거되기 때문이다.

실제로 ChannelHandler는 데이터를 변환하거나 예외를 처리하는 등 거의 모든 종류의 작업에 활용할 수 있다.

<br>

## Channel Handler의 종류

ChannelHandler는 들어온 이벤트를 처리하는 `ChannelInboundHandler`와 나가는 이벤트를 처리하는 `ChannelOutboundHandler`가 있고, `ChannelDuplexHanlder`는 나가는 것과 들어오는 것 이벤트를 두 개 다 처리하는 핸들러가 있다.

- ChannelInboundHandler
- ChannelOutboundHandler
- ChannelDuplexHandler

<br>


## 예시

### ChannelHandler 정의

> ChannelPipeline은 다음 파트에서 다룰 예정이다.

```java
b.childHanlder(new ChannelInitializer<SocketChannel>() {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new DiscardServerHandler());
    }
})
```

### DiscardServerHandler

DiscardServerHandler 클래스를 보았을때 해당 클래스는 ChannelInboundHandlerAdapter를 extends 했다.

이렇게 하면 부모의 메서드를 그대로 사용할 수 있으며 오버라이딩 할 필요 없이 부모에 구현되어있는 것을 직접 사용 가능하다.

```java
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

}
```

### ChannelInboundHandler

ChannelInboundHandler 내 무엇이 있을까?

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F26nmd%2FbtroK1g4blL%2FpHk0jM61dXvuLmUyv6Y3kk%2Fimg.png)

이벤트들이 보인다. 해당 메서드에는 전부 ChannelHandlerContext와 ChannelRead는 특별하게 Object msg 가 추가로 있다. (아마 여기에 받은 메세지가 있겠죠?)

[ChannlInboundHandler.docs](https://netty.io/4.1/api/io/netty/channel/ChannelInboundHandlerAdapter.html)

### 직접 돌려보자

```java
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(DiscardServerHandler.class.getName());

    public DiscardServerHandler() {
        Configurator.setLevel(DiscardServerHandler.class.getName(), Level.ALL);
        logger.info("DiscardServerHandler init..");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered..");
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
        logger.info("channelUnregistered..");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        logger.info("channelActive..");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        logger.info("channelInactive..");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
        logger.info("channelRead..");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
        logger.info("channelReadComplete..");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
        logger.info("userEventTriggered..");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
        logger.info("channelWritabilityChanged..");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        logger.info("exceptionCaught..");
    }

}

```

이벤트 처리 순서를 보자

상상했던대로 흘러가는 것을 볼 수 있다.

- 처음 접속했을 때 흐름: handler init -> channelRegistered -> channelActive
- 데이터가 왔을 때 흐름: channelRead -> channelReadComplete
- 접속을 끊었을 때 흐름: channelInactive -> channelUnregistered
