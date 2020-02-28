package com.rsockets.examples.rsocket.types.duplex;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class DuplexClient {

    public static void main(String[] args) {
        RSocketFactory.receive().acceptor((setup, rSocket) -> {
            rSocket.requestStream(DefaultPayload.create("Hello World")).map(str -> {
                System.out.println("Data from client side is : "+str.getDataUtf8());
                return str.getDataUtf8();
            }).log().subscribe();
            return Mono.just(new AbstractRSocket() {
            });
        }).transport(TcpServerTransport.create("localhost", 7070)).start().subscribe();


        //Client Code
        RSocket socket = RSocketFactory.connect().acceptor(
                rSocket ->
                        new AbstractRSocket() {
                            @Override
                            public Flux<Payload> requestStream(Payload payload) {
                                System.out.println("from client req stream.");
                                return Flux.interval(Duration.ofSeconds(1))
                                        .map(aLong -> DefaultPayload.create("Bi-di Response => " + aLong));
                            }
                        }).transport(TcpClientTransport.create("localhost", 7070)).start().block();

        socket.onClose().block();

    }
}
