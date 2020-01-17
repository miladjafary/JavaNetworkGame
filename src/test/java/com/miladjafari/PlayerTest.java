package com.miladjafari;

import com.miladjafari.tcp.Client;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.miladjafari.GameServerMessage.RESULT_SING_UP_SUCCESS;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class PlayerTest {

    private static final Logger logger = Logger.getLogger(PlayerTest.class);
    private static final Integer GAME_SERVER_PORT = 4444;
    private GameServer gameServer;

    @Before
    public void setup() {
        startServer();
    }

    @After
    public void tearDown() {
        gameServer.stop();
    }

    private void startServer() {
        gameServer = new GameServer();
        Thread serverThread = new Thread(() -> gameServer.start(GAME_SERVER_PORT), "GameServer Thread");
        serverThread.start();
    }

    @Test
    public void testSuccessSignUpOnePlayerToGameServer() throws InterruptedException, IOException {
        Client client = new Client();
        client.connect("localhost", GAME_SERVER_PORT);

        Player player = Player.builder()
                .name("Milad")
                .connection(client.getConnection())
                .onSingUpResponse(response -> {
                    if (response.getMessage().equals(RESULT_SING_UP_SUCCESS)) {
                        logger.info("SingUp Was Success");
                    }
                })
                .build();
        player.signUp();

        Thread.sleep(200);
        client.getConnection().close();

        assertTrue(gameServer.getPlayersConnection().containsKey("Milad"));
    }

    @Test
    public void testSuccessSignUpTwoPlayerToGameServer() throws IOException, InterruptedException {
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", GAME_SERVER_PORT);
        client2.connect("localhost", GAME_SERVER_PORT);

        Player player1 = Player.builder().name("Milad").connection(client1.getConnection()).build();
        Player player2 = Player.builder().name("Elena")
                .onSingUpResponse(response -> {
                    if (response.getMessage().equals(RESULT_SING_UP_SUCCESS)) {
                        logger.info("SingUp Was Success");
                        logger.info(String.format(
                                "Registered Players: [%s]", String.join(",",response.getPlayers())));
                    }
                })
                .connection(client2.getConnection()).build();

        player1.signUp();
        Thread.sleep(200);
        player2.signUp();

        Thread.sleep(1000);

        client1.getConnection().close();
        client2.getConnection().close();

        assertTrue(gameServer.getPlayersConnection().containsKey("Milad"));
        assertTrue(gameServer.getPlayersConnection().containsKey("Elena"));
    }

    @Test
    public void testFailSignUpPlayerIfPlayerNameIsExist() throws IOException, InterruptedException {
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", GAME_SERVER_PORT);
        client2.connect("localhost", GAME_SERVER_PORT);

        Player player1 = Player.builder().name("Milad").connection(client1.getConnection()).build();
        Player duplicatePlayer = Player.builder().name("Milad").connection(client2.getConnection()).build();

        player1.signUp();
        Thread.sleep(200);
        duplicatePlayer.signUp();

        Thread.sleep(1000);

        client1.getConnection().close();
        client2.getConnection().close();

        int expectedPlayersCount = 1;
        assertEquals(expectedPlayersCount, gameServer.getPlayersConnection().size());
        assertTrue(gameServer.getPlayersConnection().containsKey("Milad"));

    }

    @Test
    public void testSuccessSendTenMessageAndReceivedBackTenMessage() throws InterruptedException, IOException {
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", GAME_SERVER_PORT);
        client2.connect("localhost", GAME_SERVER_PORT);

        Player milad = Player.builder().name("Milad").connection(client1.getConnection()).build();
        Player elena = Player.builder().name("Elena").connection(client2.getConnection()).build();

        elena.signUp();
        Thread.sleep(200);
        milad.signUp();

        milad.playGameWith(elena.getName(), "Play with me");

        Thread.sleep(1000);

        client1.getConnection().close();
        client2.getConnection().close();

        Integer expectedReceivedMessage = 10;
        Integer expectedSendMessage = 10;

        logger.info(String.format("[%s] SEND Count: [%s]", milad.getName(), milad.getCountOfSendMessages()));
        logger.info(String.format("[%s] Received Count: [%s]", milad.getName(), milad.getCountOfReceivedMessages()));

        logger.info(String.format("[%s] SEND Count: [%s]", elena.getName(), elena.getCountOfSendMessages()));
        logger.info(String.format("[%s] Received Count: [%s]", elena.getName(), elena.getCountOfReceivedMessages()));

        assertEquals(expectedSendMessage, milad.getCountOfSendMessages());
        assertEquals(expectedReceivedMessage, milad.getCountOfReceivedMessages());

        assertEquals(expectedReceivedMessage, elena.getCountOfReceivedMessages());
        assertEquals(expectedReceivedMessage, elena.getCountOfSendMessages());
    }

    @Test
    public void testSuccessSendTwoMessageAndReceivedBackTwoMessage() throws InterruptedException, IOException {
        final Integer FINISH_THRESHOLD = 2;
        Client client1 = new Client();
        Client client2 = new Client();

        client1.connect("localhost", GAME_SERVER_PORT);
        client2.connect("localhost", GAME_SERVER_PORT);

        Player milad = Player.builder()
                .name("Milad")
                .finishThreshold(FINISH_THRESHOLD)
                .connection(client1.getConnection())
                .build();

        Player elena = Player.builder()
                .name("Elena")
                .finishThreshold(FINISH_THRESHOLD)
                .connection(client2.getConnection())
                .build();

        elena.signUp();
        Thread.sleep(200);
        milad.signUp();

        milad.playGameWith(elena.getName(), "Play with me");

        Thread.sleep(1000);

        client1.getConnection().close();
        client2.getConnection().close();

        logger.info(String.format("[%s] SEND Count: [%s]", milad.getName(), milad.getCountOfSendMessages()));
        logger.info(String.format("[%s] Received Count: [%s]", milad.getName(), milad.getCountOfReceivedMessages()));

        logger.info(String.format("[%s] SEND Count: [%s]", elena.getName(), elena.getCountOfSendMessages()));
        logger.info(String.format("[%s] Received Count: [%s]", elena.getName(), elena.getCountOfReceivedMessages()));

        assertEquals(FINISH_THRESHOLD, milad.getCountOfSendMessages());
        assertEquals(FINISH_THRESHOLD, milad.getCountOfReceivedMessages());

        assertEquals(FINISH_THRESHOLD, elena.getCountOfReceivedMessages());
        assertEquals(FINISH_THRESHOLD, elena.getCountOfSendMessages());
    }

}