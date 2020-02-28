package com.rsockets.examples.random.ex1;

import io.rsocket.*;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class HelloWorldSocketAcceptor implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        return Mono.just(new AbstractRSocket() {
            @Override
            public Mono<Void> fireAndForget(Payload payload) {
                return Mono.empty();
            }

            @Override
            public Mono<Payload> requestResponse(Payload payload) {
                System.out.println("Request for requestResponse : "+payload);
                return Mono.just(DefaultPayload.create("Hello "+payload.getDataUtf8()));
            }

            @Override
            public Flux<Payload> requestStream(Payload payload) {
                return Flux.interval(Duration.ofMillis(1000)).map(time -> DefaultPayload.create("Hello "+payload.getDataUtf8()+" @ "+time));
            }

            @Override
            public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                return Flux.from(payloads).map(payload -> DefaultPayload.create("Hello "+payload.getDataUtf8())).subscribeOn(Schedulers.parallel());
            }

            @Override
            public Mono<Void> metadataPush(Payload payload) {
                return Mono.empty();
            }
        });
    }
}
