package com.miladjafari;

import com.miladjafari.tcp.Client;
import com.miladjafari.tcp.Connection;

public class GameRunner {
    private static final String GAME_SERVER_HOST = "localhost";
    private static final Integer GAME_SERVER_PORT = 4444;

    private GameServer gameServer;

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

    private Player createPlayer(String name) {
        Connection connection = openConnectionToGameServer(GAME_SERVER_HOST, GAME_SERVER_PORT);
        return Player.builder().name(name).connection(connection).build();
    }

    public static void main(String[] args) {
        GameRunner gameRunner = new GameRunner();
        gameRunner.startServer();

        Player elena = gameRunner.createPlayer("Elena");
        elena.signUp();

        Player milad = gameRunner.createPlayer("Milad");
        milad.signUp();
        milad.playGameWith(elena.getName(),"PlayWithMe");
    }
}
