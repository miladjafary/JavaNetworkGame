package com.miladjafari;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {

    private Server server;
    private Client client;

    @Before
    public void before() {
        startServer();
        client = new Client();
        client.connect("localhost", 4444);
    }

    private void startServer() {
        server = Server.builder()
                .port(4444)
                .onConnection(connection -> {
                    connection.onMessage(message ->
                            System.out.println(String.format("Test Server - Connection Received [%s]", message)));

                    System.out.println(String.format("Test Server - Incoming connection [%s][%s]",
                            connection.getHost(),
                            connection.getPort())
                    );
                })
                .build();

        Thread serverThread = new Thread(() -> server.start(), "ServerThread");
        serverThread.start();
    }

    @After
    public void after() throws IOException {
        client.getConnection().close();
    }

    @Test
    public void testEcho() {
        String response1 = client.getConnection().sendMessage("Msg1");
        String response2 = client.getConnection().sendMessage("Msg2");
        String response3 = client.getConnection().sendMessage("Msg3");
        String response4 = client.getConnection().sendMessage("!");
        String response5 = client.getConnection().sendMessage(".");

        assertEquals("Msg1", response1);
        assertEquals("Msg2", response2);
        assertEquals("Msg3", response3);
        assertEquals("!", response4);
        assertEquals("good bye", response5);
    }
}