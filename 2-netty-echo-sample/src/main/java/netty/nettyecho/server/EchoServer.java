package netty.nettyecho.server;

public class EchoServer {

    private final int port;


    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer(5001).start();

    }

    private void start() throws InterruptedException {

    }
}
