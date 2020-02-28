package com.rsockets.examples.rsocket.types.reqresp;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class RequestResp {

    public static void main(String[] args) {
        RSocketFactory.receive().acceptor((setup, rSocket) -> {
            return Mono.just(new AbstractRSocket() {
                boolean fail = true;

                @Override
                public Mono<Payload> requestResponse(Payload p) {
                    if (fail) {
                        fail = false;
                        return Mono.error(new Throwable());
                    } else {
                        return Mono.just(p);
                    }
                }
            });
        }).transport(TcpServerTransport.
                create("localhost", 7070)).start().subscribe();

        RSocket socket = RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7070)).start().block();

        socket.requestResponse(DefaultPayload.create("Hello World")).map(Payload::getDataUtf8).onErrorReturn("error").doOnNext(System.out::println).block();

        socket.requestResponse(DefaultPayload.create("Hello World")).map(Payload::getDataUtf8).onErrorReturn("error").doOnNext(System.out::println).block();
        socket.dispose();
    }

}
