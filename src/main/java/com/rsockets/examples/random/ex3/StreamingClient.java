package com.rsockets.examples.random.ex3;

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

public class StreamingClient {

    public static void main(String[] args) {
        RSocketFactory.receive().acceptor(((connectionSetupPayload, rSocket) -> Mono.just(new AbstractRSocket() {
            @Override
            public Flux<Payload> requestStream(Payload payload) {
                return Flux.interval(Duration.ofMillis(100))
                        .map(aLong -> DefaultPayload.create("Interval: " + aLong));
            }
        }))).transport(TcpServerTransport.create("localhost", 7070)).start().subscribe();

        RSocket socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7070)).start().block();

        socket.requestStream(DefaultPayload.create("Hello World")).map(Payload::getDataUtf8).doOnNext(System.out::println).take(10).then().doFinally(signalType -> socket.dispose()).block();


    }
}
