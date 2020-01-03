package com.miladjafari;


import org.apache.log4j.Logger;

import static com.miladjafari.GameServer.CMD_PLAY_REQUEST;
import static com.miladjafari.GameServer.CMD_PLAY_RESPONSE;

public class Player {
    private static final Logger logger = Logger.getLogger(Player.class);
    private String name;
    private Integer countOfReceivedMessages = 0;
    private Integer countOfSendMessages = 0;

    private Connection connection;

    public Player(String name, Connection connection) {
        this.name = name;
        this.connection = connection;
        this.connection.onMessage(message -> {
            logReceivedRequest(message);
            handleServerMessage(message);
        });
    }

    public String getName() {
        return name;
    }

    private void logSendRequest(String log) {
        logger.info(String.format("[%s] Send: [%s]", name, log));
    }

    private void logReceivedRequest(String log) {
        logger.info(String.format("[%s] Received: [%s]", name, log));
    }


    public void signUp() {
        String singUpRequest = GameServerMessage.builder().playerName(name).buildSignUpCommand().encodeToString();
        connection.sendMessage(singUpRequest);
        logSendRequest(singUpRequest);
    }

    public void playGameWith(Player opponent) {
        String playRequest = GameServerMessage.builder()
                .playerName(name)
                .opponentName(opponent.getName())
                .message("Play with me")
                .buildPlayRequestCommand()
                .encodeToString();

        countOfSendMessages++;
        connection.sendMessage(playRequest);
        logSendRequest(playRequest);
    }

    public void handleServerMessage(String message) {
        GameServerMessage gameServerMessage = GameServerMessage.builder().serverMessage(message).build();

        String command = gameServerMessage.getCommand();
        switch (command) {
            case CMD_PLAY_REQUEST:
                sendReply(gameServerMessage);
                break;
            case CMD_PLAY_RESPONSE:
                sendRequest(gameServerMessage);
                break;
        }
    }

    public void sendReply(GameServerMessage incomingMessage) {
        countOfReceivedMessages++;
        String response = GameServerMessage.builder()
                .incomingMessage(incomingMessage)
                .countOfReceivedMessage(countOfReceivedMessages)
                .buildPlayResponseCommand()
                .encodeToString();
        connection.sendMessage(response);
    }

    public void sendRequest(GameServerMessage incomingMessage) {
        int getCountOfPlayerReceivedMessage = Integer.parseInt(incomingMessage.getCountOfPlayerReceivedMessage());
        if (getCountOfPlayerReceivedMessage < 10) {
            countOfSendMessages++;
            String playRequest = GameServerMessage.builder()
                    .incomingMessage(incomingMessage)
                    .message(incomingMessage.getMessage() + ":" + countOfSendMessages)
                    .buildPlayRequestCommand()
                    .encodeToString();
            connection.sendMessage(playRequest);

            logSendRequest(playRequest);
        }
    }
}
