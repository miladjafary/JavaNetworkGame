package com.miladjafari;

import com.miladjafari.tcp.Client;
import com.miladjafari.tcp.Connection;
import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRunner {
    private static final Logger logger = Logger.getLogger(GameServer.class);

    private static final String GAME_SERVER_HOST = "localhost";
    private static final Integer GAME_SERVER_PORT = 4444;
    private static final Integer MAX_THREADS = 30;

    private GameServer gameServer;

    public void startServer() {
        gameServer = new GameServer();
        Thread serverThread = new Thread(() -> gameServer.start(GAME_SERVER_PORT), "GameServer Thread");
        serverThread.start();
    }

    public Connection openConnectionToGameServer(String host, Integer port) {
        Client client = new Client();
        client.connect(host, port);
        return client.getConnection();
    }

    private Player createPlayer(String name) {
        Connection connection = openConnectionToGameServer(GAME_SERVER_HOST, GAME_SERVER_PORT);
        Player player = Player.builder().name(name).connection(connection).build();

        return player;
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        GameRunner gameRunner = new GameRunner();
        gameRunner.startServer();

        Player milad = gameRunner.createPlayer("Milad");
        Player elena = gameRunner.createPlayer("Elena");

        milad.signUp();
        Thread.sleep(100);
        elena.signUp();
//
//
        milad.playGameWith(elena.getName(),"PlayWithMe");


    }
}
