package com.miladjafari;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private static final Logger logger = Logger.getLogger("GameServer");

    private Map<String, Connection> playersConnection = new HashMap<>();

    public static final String CMD_SIGN_UP = "signUp";
    public static final String CMD_PLAY_REQUEST = "playRequest";
    public static final String CMD_PLAY_RESPONSE = "playResponse";

    public Map<String, Connection> getPlayersConnection() {
        return playersConnection;
    }

    public void start() {
        Server server = Server.builder()
                .port(4444)
                .onConnection(connection -> {
                    logger.info(String.format("Incoming connection [%s][%s]",
                            connection.getHost(),
                            connection.getPort())
                    );

                    connection.onMessage(message -> handleMessage(message, connection));
                })
                .build();
        server.start();
    }

    private void handleMessage(String message, Connection connection) {
        GameServerMessage serverMessage = GameServerMessage.builder().serverMessage(message).build();


        switch (serverMessage.getCommand()) {
            case CMD_SIGN_UP:
                signUpPlayer(serverMessage.getPlayerName(), connection);
                break;
            case CMD_PLAY_REQUEST:
                sendPlayRequest(serverMessage);
                break;
            case CMD_PLAY_RESPONSE:
                sendPlayResponse(serverMessage);
                break;
        }
    }

    private void signUpPlayer(String playerName, Connection playerConnection) {
        playersConnection.putIfAbsent(playerName, playerConnection);
        logger.debug(String.format("SingUp [%s]", playerName));
    }

    private void sendPlayRequest(GameServerMessage serverMessage) {
        String opponentName = serverMessage.getOpponentName();
        logReceivedMessage(serverMessage.getPlayerName(), serverMessage);
        sendMessageToPlayer(opponentName, serverMessage);
    }

    private void sendPlayResponse(GameServerMessage serverMessage) {
        String playerName = serverMessage.getPlayerName();
        logReceivedMessage(serverMessage.getOpponentName(), serverMessage);
        sendMessageToPlayer(playerName, serverMessage);
    }

    private void sendMessageToPlayer(String playerName, GameServerMessage serverMessage) {
        if (playersConnection.containsKey(playerName)) {
            Connection opponentConnection = playersConnection.get(playerName);
            opponentConnection.sendMessage(serverMessage.encodeToString());
            logSendMessage(playerName, serverMessage);
        } else {
            logger.warn(String.format("Can not find any connection for player [%s]", playerName));
        }
    }

    private void logReceivedMessage(String playerName, GameServerMessage serverMessage) {
        logger.info(String.format("Received [%s]: [%s]", playerName, serverMessage.encodeToString()));
    }

    private void logSendMessage(String playerName, GameServerMessage serverMessage) {
        logger.info(String.format("Send     [%s]: [%s]", playerName, serverMessage.encodeToString()));
    }
}
