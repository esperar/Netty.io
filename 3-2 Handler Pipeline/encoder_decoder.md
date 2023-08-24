# 인코더와 디코더

Netty로 메시지를 전송하거나 수신할 때는 데이터를 변환해야 한다.

**인바운드 메시지**는 바이트에서 다른 포맷으로(보통 자바 객체) 변환되는 **디코딩**을 거친다.

**아웃바운드 메시지**는 반대로 현재 포맷에서 바이트로 **인코딩** 된다.

이러한 두 가지 변환이 필요한 이유는 네트워크 데이터는 반드시 연속된 바이트여야하기 때문이다.

<br>

## 사용자 정의 인코더, 디코더

사용자 정의 인코더, 디코더를 작성하려면 `ChannelInboundHandlerAdapter`, `ChannelOutboundHandlerAdapter` 클래스를 상속받아 구현한다.

각각의 클래스에서는 channelRead(), write() 메서드를 오버라이딩 해서 사용자 정의 로직을 구현한다.

### Decoder

```java
public class CustomDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        // 데이터 디코딩 로직 구현
        String decodedData = decodeData(buf);
        // 디코딩된 데이터를 다음 핸들러로 전달
        ctx.fireChannelRead(decodedData);
    }
    
    private String decodeData(ByteBuf buf) {
        // 데이터 디코딩 로직 구현
        return decodedData;
    }
}
```

### Encoder

```java
public class CustomEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 데이터 인코딩 로직 구현
        ByteBuf encodedData = encodeData(msg);
        // 인코딩된 데이터를 다음 핸들러로 전달
        ctx.write(encodedData, promise);
    }
    
    private ByteBuf encodeData(Object data) {
        // 데이터 인코딩 로직 구현
        return encodedData;
    }
}
```