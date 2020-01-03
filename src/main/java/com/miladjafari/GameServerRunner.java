package com.miladjafari;

public class GameServerRunner {

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.start();
    }

    public static void main1(String[] args) {
        Server server = Server.builder()
                .port(4444)
                .onConnection(connection -> {
                    connection.onMessage(message -> {
                        System.out.println(String.format("Test Server - Connection Received [%s]", message));
                        connection.sendMessage(message);
                        connection.sendMessage("KISS ASS");
                    });

                    System.out.println(String.format("Test Server - Incoming connection [%s][%s]",
                            connection.getHost(),
                            connection.getPort())
                    );
                })
                .build();
        server.start();
    }
}
