package com.rsockets.examples.random.ex2;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

public class Client {

    private final RSocket socket;

    public Client() {
        this.socket = RSocketFactory.connect().
                transport(TcpClientTransport.create("localhost", 9090)).start().block();
    }

    public String callBlocking(String string) {
        return socket.requestResponse(DefaultPayload.create(string)).map(Payload::getDataUtf8).block();
    }

    public Flux<Float> getDataStream() {
        return socket.requestStream(DefaultPayload.create("Hi There Need Stream")).map(Payload::getData).map(buf -> buf.getFloat())
                .onErrorReturn(null);
    }

    public void dispose() {
        this.socket.dispose();
    }

}
