## @Sharable

: 이 어노테이션이 붙은 클래스의 인스턴스는 여러 Channel에서 공유할 수 있음을 나타낸다.

<br>

## SimpleChannelInboundHanlder vs ChannelInboundHanlder 

Client에서는 `SImpleChannelInboundHandler`를 사용했다.

클라이언트에서 channelRead0()이 완료된 시점에서 돌아오는 메시지는 이미 확보된 상태이며 메서드가 반환될 때 ByteBuf에 대한 메모리 참조를 해제한다.

반면 서버에서는 ChannelRead()가 반환될 때까지 비동기의 write() 작업이 완료되지 않았을 수 있다.

이 메시지는 channelReadComplete()에서 writeAndFlush()가 호출될 때 헤제된다.

이처럼 서로 메모리 해제하는 구간이 다르고, 메서드의 약간의 차이가 있으므로

Client에서는 SimpleChannelInboundHandler를, Server에서는 ChannelInboundHandler를 사용하는 것으로 기억해두고 넘어가자.