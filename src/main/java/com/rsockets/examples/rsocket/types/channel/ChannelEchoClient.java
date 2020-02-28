package com.rsockets.examples.rsocket.types.channel;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.ByteBufPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class ChannelEchoClient {

    private static final Payload payload = ByteBufPayload.create("Hello ");

    public static void main(String[] args) {
        RSocketFactory.receive()
                .acceptor(new SocketAcceptorImpl())
                .transport(TcpServerTransport.create("localhost", 7070))
                .start()
                .subscribe();

        RSocket socket = RSocketFactory.connect().keepAliveAckTimeout(Duration.ofMinutes(1)).transport(TcpClientTransport.create("localhost",7070)).start().block();

        Flux.range(0, 10000)
                .concatMap(i -> socket.fireAndForget(payload.retain())).blockLast()
//                        .doOnNext(p -> {
//                            System.out.println(p.getDataUtf8());
//                            p.release();
//                        })
                ;
    }


    private static class SocketAcceptorImpl implements SocketAcceptor {
        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket reactiveSocket) {
            return Mono.just(
                    new AbstractRSocket() {

                        @Override
                        public Mono<Void> fireAndForget(Payload payload) {
                            System.out.println(payload.getDataUtf8());
                            payload.release();
                            System.out.println("MetaData "+payload.getDataUtf8());
                            return Mono.empty();
                        }

                        @Override
                        public Mono<Payload> requestResponse(Payload payload) {
                            return Mono.just(payload);
                        }

                        @Override
                        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                            return Flux.from(payloads).subscribeOn(Schedulers.single());
                        }
                    });
        }
    }
}
