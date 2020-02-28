package com.rsockets.examples.random.ex1;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;

public class HelloWorldSocketTest {

    public static void main(String[] args) {

        RSocketFactory.receive().acceptor(new HelloWorldSocketAcceptor()).
                transport(TcpServerTransport.create("localhost", 8082)).start().subscribe();

        RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 8082))
                .start()
                .block().requestResponse(DefaultPayload.create("Chaitanya..")).subscribe(payload -> {
                    System.out.println("Response is : "+payload.getDataUtf8());
                });

        System.out.println("started both server and client...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
