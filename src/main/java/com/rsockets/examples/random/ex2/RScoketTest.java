package com.rsockets.examples.random.ex2;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

public class RScoketTest {
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Starting rSocket client.");

        Client client = new Client();
        String resp = client.callBlocking("Hi Client");
        System.out.println("Response from Client is : "+resp);

        System.out.println("Reading from Stream");
        RSocketStreamClient streamClient = new RSocketStreamClient();
        Flux<Float> streamResp = streamClient.getDataStream();
        System.out.println("Stream Resp is : "+streamResp.log());
        streamResp.subscribe(val -> System.out.println(" Value : "+val));
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class RSocketStreamClient {

        private final RSocket socket;

        public RSocketStreamClient() {
            socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 9090)).start().block();
        }

        public Flux<Float> getDataStream() {
            return socket.requestStream(DefaultPayload.create("Hi There Need Stream")).map(Payload::getData).map(buf -> buf.getFloat())
                    .onErrorReturn(null);
        }

        public void dispose() {
            socket.dispose();
        }
    }
}
