package com.rsockets.examples.random.ex2;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Server {

    private final Disposable server;

    int TCP_PORT = 9090;
    public Server() {
        this.server = RSocketFactory.receive().acceptor((connectionSetupPayload, rSocket) -> Mono.just(new RSocketImpl()))
        .transport(TcpServerTransport.create("localhost", TCP_PORT)).start().subscribe();
    }

    public void dispose() {
        this.server.dispose();
    }

    public class RSocketImpl extends AbstractRSocket {
        @Override
        public Mono<Payload> requestResponse(Payload payload) {
            try {
                return Mono.just(payload);
            } catch (Exception exp) {
                return Mono.error(exp);
            }
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            System.out.println("Requested for a Stream!!!");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Flux.just(payload, payload);
        }
    }

}
