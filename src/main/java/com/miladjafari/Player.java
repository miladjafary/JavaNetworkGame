package com.miladjafari;


import com.miladjafari.tcp.Connection;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

import static com.miladjafari.GameServerMessage.*;

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

    private Consumer<GameServerMessage> onSingUpResponse = singUpResponse -> {
        String logMessage = singUpResponse.getMessage();
        if (singUpResponse.getMessage().equals(RESULT_SING_UP_SUCCESS)) {
            logMessage = String.format("Successful. Registered Players %s", singUpResponse.getPlayers());
        }

        logger.info(String.format("[%s] SingUp result: %s ", name, logMessage));
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

    private void infoLogRequest(String log) {
        logger.info(String.format("[%s]: [%s]", name, log));
    }

    private void logSendRequest(String log) {
        logger.debug(String.format("[%s] Send: [%s]", name, log));
    }

    private void logReceivedRequest(String log) {
        logger.debug(String.format("[%s] Received: [%s]", name, log));
    }

    public void signUp() {
        String singUpRequest = GameServerMessage.builder().playerName(name).buildSignUpRequestCommand().encodeToString();
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
        infoLogRequest(message);
    }

    public void handleServerMessage(String message) {
        GameServerMessage gameServerMessage = GameServerMessage.builder().serverMessage(message).build();

        String command = gameServerMessage.getCommand();
        switch (command) {
            case CMD_SIGN_UP_RESPONSE:
                handleSingUpResponse(gameServerMessage);
                break;
            case CMD_PLAY_REQUEST:
                increaseReceivedMessageCount();
                handlePlayRequestCommand(gameServerMessage);
                break;
            case CMD_PLAY_RESPONSE:
                increaseReceivedMessageCount();
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

    private void handleSingUpResponse(GameServerMessage gameServerMessage) {
        onSingUpResponse.accept(gameServerMessage);
    }

    private void handlePlayRequestCommand(GameServerMessage incomingMessage) {
        String message = incomingMessage.getMessage() + countOfSendMessages;
        String response = GameServerMessage.builder()
                .incomingMessage(incomingMessage)
                .message(message)
                .countOfReceivedMessage(countOfReceivedMessages)
                .buildPlayResponseCommand()
                .encodeToString();

        connection.sendMessage(response);
        increaseSendMessageCount();

        infoLogRequest(message);
        logSendRequest(response);
    }

    private void handlePlayResponseCommand(GameServerMessage incomingMessage) {
        if (countOfReceivedMessages < finishThreshold) {
            String message = incomingMessage.getMessage() + countOfSendMessages;
            String playRequest = GameServerMessage.builder()
                    .incomingMessage(incomingMessage)
                    .message(message)
                    .buildPlayRequestCommand()
                    .encodeToString();
            connection.sendMessage(playRequest);

            increaseSendMessageCount();

            infoLogRequest(message);
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

        public Builder onSingUpResponse(Consumer<GameServerMessage> onSingUpResponse) {
            player.onSingUpResponse = onSingUpResponse;
            return this;
        }

        public Player build() {
            return player;
        }
    }
}
