# 네티의 핵심 컴포넌트

## Channel

하나 이상의 입출력 작업(읽기 또는 쓰기)을 수행할 수 있는 하드웨어 장치, 파일, 네트워크 소켓, 프로그램 컴포넌트와 같은 엔티티에 대한 열린 연결

일단 Channel을 들어오는 데이터와 나가는 데이터를 위한 운송수단이라고 생각하자. Channel은 열거나 닫고, 연결하거나 연결을 끊을 수 있다.

<br>

## 콜백

콜백은 간단히 말해 다른 메서드로 자신에 대한 참조를 제공할 수 있는 메서드다.  
콜백은 관심 대상에게 작업 완료를 알리는 가장 일반적인 방법 중 하나다.

네티는 이벤트를 처리할 때 내부적으로 콜백을 허용한다. 콜백이 트리거 되면 `ChannelHandler` 인터페이스의 구현을 통해 이벤트를 처리할 수 있다.

<br>

## Future

Future는 작업이 완료되면 이를 애플리케이션에 알리는 한 방법이다. 이 객체는 비동기 작업의 결과를 담는 자리표시자 역할을 하며, 미래의 어떤 시점에 작업이 완료되면 그 결과에 접근할 수 있게 해준다.

JDK는 Future를 제공하지만, 수동으로 작업완료 여부를 확인하거나 완료되기 전까지 블로킹하는 기능만 있다.

따라서 네티는 **비동기 작업**이 실행됐을 때 이용할 수 있는 자체 구현 `ChannelFuture`을 제공한다.

앞서 언급한 대로 네티는 기본적으로 비동기식이며 이벤트 기반이다.

### ChannelFutureListener 활용

`ChannelFutureListener`를 활용하는 예제를 작성해보겠다.

먼저 원격 피어로 연결한 다음, connect() 호출로 반횐된 ChannelFuture를 이용해 새로운 ChannelFutureListener를 등록한다.

작업이 정상적이면 데이터를 Channel로 기록하며, 그렇지 않으면 ChannelFuture에서 Throwable를 가져온다.

```java
Channel channel = ...;

// 볼로킹 x
ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));

future.addListener(new ChannelFutureListener()) {
    @Override
    public void operationComplete(ChannelFuture future) {
        if(future.isSuccess()){
            ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
            ChannelFuture wf = future.channel().writeAndFlush(buffer);
        } else {
            Throwable cause = future.cause();
            cause.printStacTace()
        }
    }
}
```

<br>

## 이벤트와 핸들러

네티는 **작업의 상태 변화**를 알리기 위해 고유한 **이벤트를 이용**하며, 발생한 이벤트를 기준으로 적절한 동작을 트리거할 수 있다. 

다음과 같은 동작이 포함된다.

- 로깅
- 데이터 변환
- 흐름 제어
- 애플리케이션 논리

네티는 네트워크 프레임워크이므로 이벤트 역시 **인바운드 또는 아웃바운드 데이터 흐름**에 대한 연관성을 기준으로 분류된다.

**인바운드** 데이터나 연관된 상태 변화로 트리거되는 이벤트는 다음을 포함한다.

- 연결 활성화 또는 비활성화
- 데이터 읽기
- 사용자 이벤트
- 오류 이벤트

**아웃바운드** 이벤트는 다음과 같이 미래에 한 동작을 트리거하는 작업의 결과들이다.

- 원격 피어로 연결 열기 또는 닫기
- 소켓으로 데이터 쓰기 또는 플러시

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FWFLOW%2FbtqQt6i4Tzp%2FtvrWVgBuTfKkZrb5F7CXl1%2Fimg.jpg)

