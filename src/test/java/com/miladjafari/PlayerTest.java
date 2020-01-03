package com.miladjafari;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class PlayerTest {
    private GameServer gameServer;


    @Before
    public void before() {
        startServer();
    }

    private void startServer() {
//        gameServer = new GameServer();
//        Thread serverThread = new Thread(() -> gameServer.start(), "GameServer Thread");
//        serverThread.start();
    }

    @Test
    public void testSuccessSignUpOnePlayerToGameServer() throws InterruptedException {
        Client client = new Client();
        client.connect("localhost", 4444);

        Player player = new Player("Milad", client.getConnection());
        player.signUp();

        Thread.sleep(100);
        assertTrue(gameServer.getPlayersConnection().containsKey("Milad"));
    }


    @Test
    public void testSuccessSignUpTwoPlayerToGameServer() {
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", 4444);
        client2.connect("localhost", 4444);

        Player player1 = new Player("Milad", client1.getConnection());
        Player player2 = new Player("Elena", client2.getConnection());

        player1.signUp();
        player2.signUp();

        assertTrue(gameServer.getPlayersConnection().containsKey("Milad"));
        assertTrue(gameServer.getPlayersConnection().containsKey("Elena"));
    }

    @Test
    public void testSuccessPlayWithOpponent() throws InterruptedException {
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", 4444);
        client2.connect("localhost", 4444);

        Player milad = new Player("Milad", client1.getConnection());
        Player elena = new Player("Elena", client2.getConnection());

        elena.signUp();
        Thread.sleep(200);
        milad.signUp();

        milad.playGameWith(elena);

        Thread.sleep(1000);
    }
}