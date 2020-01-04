package com.miladjafari;


import com.miladjafari.tcp.Connection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.function.Consumer;

import static com.miladjafari.GameServer.CMD_PLAY_REQUEST;
import static com.miladjafari.GameServer.CMD_PLAY_RESPONSE;

public class Player {
    private static final Logger logger = Logger.getLogger(Player.class);

    private String name;

    private Integer finishThreshold = 10;
    private Integer countOfReceivedMessages = 0;
    private Integer countOfSendMessages = 0;

    private Connection connection;
    private Consumer<String> onReceivedMessage = message -> {
        logReceivedRequest(message);
        handleServerMessage(message);
    };

    public String getName() {
        return name;
    }

    public Integer getFinishThreshold() {
        return finishThreshold;
    }

    public Integer getCountOfReceivedMessages() {
        return countOfReceivedMessages;
    }

    public Integer getCountOfSendMessages() {
        return countOfSendMessages;
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

    public void playGameWith(String opponent, String message) {
        String playRequest = GameServerMessage.builder()
                .playerName(name)
                .opponentName(opponent)
                .message(message)
                .buildPlayRequestCommand()
                .encodeToString();

        connection.sendMessage(playRequest);
        increaseSendMessageCount();

        logSendRequest(playRequest);
    }

    public void handleServerMessage(String message) {
        increaseReceivedMessageCount();
        GameServerMessage gameServerMessage = GameServerMessage.builder().serverMessage(message).build();

        String command = gameServerMessage.getCommand();
        switch (command) {
            case CMD_PLAY_REQUEST:
                handlePlayRequestCommand(gameServerMessage);
                break;
            case CMD_PLAY_RESPONSE:
                handlePlayResponseCommand(gameServerMessage);
                break;
        }
    }

    public void increaseSendMessageCount() {
        countOfSendMessages++;
    }

    public void increaseReceivedMessageCount() {
        countOfReceivedMessages++;
    }

    private void handlePlayRequestCommand(GameServerMessage incomingMessage) {
        String response = GameServerMessage.builder()
                .incomingMessage(incomingMessage)
                .countOfReceivedMessage(countOfReceivedMessages)
                .buildPlayResponseCommand()
                .encodeToString();

        connection.sendMessage(response);
        increaseSendMessageCount();
    }

    private void handlePlayResponseCommand(GameServerMessage incomingMessage) {
        if (countOfReceivedMessages < finishThreshold) {
            String playRequest = GameServerMessage.builder()
                    .incomingMessage(incomingMessage)
                    .message(incomingMessage.getMessage() + ":" + countOfSendMessages)
                    .buildPlayRequestCommand()
                    .encodeToString();
            connection.sendMessage(playRequest);

            increaseSendMessageCount();
            logSendRequest(playRequest);
        } else {
            gameOver();
        }
    }

    public void gameOver() {
        logger.info(String.format("[%s] Game Over!", name));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Player player = new Player();

        public Builder name(String name) {
            player.name = name;
            return this;
        }

        public Builder finishThreshold(Integer finishThreshold) {
            player.finishThreshold = finishThreshold;
            return this;
        }

        public Builder connection(Connection connection) {
            player.connection = connection;
            player.connection.onMessage(player.onReceivedMessage);
            return this;
        }

        public Player build() {
            return player;
        }
    }
}
