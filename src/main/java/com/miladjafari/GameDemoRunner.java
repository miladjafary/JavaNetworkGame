package com.miladjafari;

import com.miladjafari.tcp.Client;
import com.miladjafari.tcp.Connection;

public class GameDemoRunner {
    private static final String GAME_SERVER_HOST = "localhost";
    private static final Integer GAME_SERVER_PORT = 4444;

    private GameServer gameServer;

    private Player elena;
    private Player milad;

    public void startServer() {
        gameServer = new GameServer();
        Thread serverThread = new Thread(() -> gameServer.start(GAME_SERVER_PORT), "GameServer Thread");
        serverThread.start();
    }

    public void stopServer() {
        gameServer.stop();
    }

    public Connection openConnectionToGameServer(String host, Integer port) {
        Client client = new Client();
        client.connect(host, port);
        return client.getConnection();
    }

    private void run() {
        startServer();

        Connection player1Connection = openConnectionToGameServer(GAME_SERVER_HOST, GAME_SERVER_PORT);
        elena = Player.builder()
                .name("Elena")
                .connection(player1Connection)
                .build();
        elena.signUp();

        Connection connection = openConnectionToGameServer(GAME_SERVER_HOST, GAME_SERVER_PORT);
        milad = Player.builder()
                .name("Milad")
                .connection(connection)
                .onSingUpResponse(gameServerMessage -> milad.playGameWith(elena.getName(), "Hello"))
                .build();
        milad.signUp();
    }

    public static void main(String[] args) {
        GameDemoRunner gameDemoRunner = new GameDemoRunner();
        gameDemoRunner.run();
    }
}
