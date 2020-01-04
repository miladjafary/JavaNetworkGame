package com.miladjafari.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    private Server echoServer;
    private Client client;

    @Before
    public void before() {
        startServer();

        client = new Client();
        client.connect("localhost", 4444);
    }

    @After
    public void tearDown() throws IOException {
        client.getConnection().close();
        echoServer.stop();
    }

    private void startServer() {
        echoServer = Server.builder()
                .port(4444)
                .onConnection(connection -> {
                    connection.onMessage(message -> {
                        System.out.println(String.format("Test Server - Connection Received [%s]", message));
                        connection.sendMessage("Reply|" + message);
                    });

                    System.out.println(String.format("Test Server - Incoming connection [%s][%s]",
                            connection.getHost(),
                            connection.getPort())
                    );
                })
                .build();

        Thread serverThread = new Thread(() -> echoServer.start(), "ServerThread");
        serverThread.start();
    }

    @Test
    public void testEchoServer() throws InterruptedException {
        List<String> expectedResponseList = new ArrayList<String>() {{
            add("Reply|Msg1");
            add("Reply|Msg2");
            add("Reply|Msg3");
        }};

        List<String> actualResponseList = new ArrayList<>();
        client.getConnection().onMessage(message -> {
            System.out.println("Test Client - Received: " + message);
            actualResponseList.add(message);
        });

        client.getConnection().sendMessage("Msg1");
        client.getConnection().sendMessage("Msg2");
        client.getConnection().sendMessage("Msg3");

        Thread.sleep(1000);

        for (int i = 0; i < expectedResponseList.size(); i++) {
            String expected = expectedResponseList.get(i);
            String actual = actualResponseList.get(i);

            assertEquals(expected, actual);
        }
    }
}