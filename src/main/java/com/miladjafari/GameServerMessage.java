package com.miladjafari;

import java.util.Optional;

public class GameServerMessage {
    private String command;
    private String payload;

    private String playerName = "";
    private String opponentName = "";
    private String message = "";
    private String countOfPlayerReceivedMessage = "0";

    private static final String SEPARATOR = "|";

    public GameServerMessage() {
    }

    public GameServerMessage(String serverMessage) {
        String[] messageParts = serverMessage.split("\\" + SEPARATOR);
        command = messageParts[0];
        payload = messageParts[1];
    }

    public String getCommand() {
        return command;
    }

    public String getPayload() {
        return payload;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getMessage() {
        return message;
    }

    public String getCountOfPlayerReceivedMessage() {
        return countOfPlayerReceivedMessage;
    }

    public String encodeToString() {
        return command + SEPARATOR + payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private GameServerMessage instance = new GameServerMessage();

        public Builder serverMessage(String serverMessage) {
            String[] messageFields = serverMessage.split("\\" + GameServerMessage.SEPARATOR);
            instance.command = messageFields[0];

            switch (instance.command) {
                case GameServer.CMD_SIGN_UP:
                    setSignUpCommandFields(messageFields);
                    break;
                case GameServer.CMD_PLAY_REQUEST:
                    setPlayRequestCommandFields(messageFields);
                    break;
                case GameServer.CMD_PLAY_RESPONSE:
                    setPlayResponseCommandFields(messageFields);
                    break;
            }

            return this;
        }

        public Builder incomingMessage(GameServerMessage gameServerMessage) {
            playerName(gameServerMessage.getPlayerName());
            opponentName(gameServerMessage.getOpponentName());
            message(gameServerMessage.getMessage());
            instance.countOfPlayerReceivedMessage = gameServerMessage.getCountOfPlayerReceivedMessage();

            return this;
        }

        public Builder playerName(String playerName) {
            instance.playerName = playerName;
            return this;
        }

        public Builder opponentName(String opponentName) {
            instance.opponentName = opponentName;
            return this;
        }

        public Builder message(String message) {
            instance.message = message;
            return this;
        }

        public Builder countOfReceivedMessage(Integer countOfPlayerReceivedMessage) {
            instance.countOfPlayerReceivedMessage = Optional.ofNullable(countOfPlayerReceivedMessage)
                    .map(Object::toString)
                    .orElse("0");
            return this;
        }

        private void setSignUpCommandFields(String[] messageFields) {
            instance.playerName = messageFields[1];

            buildSignUpCommand();
        }

        private void setPlayRequestCommandFields(String[] messageFields) {
            instance.playerName = messageFields[1];
            instance.opponentName = messageFields[2];
            instance.message = messageFields[3];

            buildPlayRequestCommand();
        }

        private void setPlayResponseCommandFields(String[] messageFields) {
            instance.playerName = messageFields[1];
            instance.opponentName = messageFields[2];
            instance.message = messageFields[3];
            instance.countOfPlayerReceivedMessage = messageFields[4];

            buildPlayResponseCommand();
        }

        public GameServerMessage buildSignUpCommand() {
            instance.command = GameServer.CMD_SIGN_UP;
            instance.payload = instance.playerName;
            return instance;
        }

        public GameServerMessage buildPlayRequestCommand() {
            instance.command = GameServer.CMD_PLAY_REQUEST;
            instance.payload = String.format("%s|%s|%s", instance.playerName, instance.opponentName, instance.message);

            return instance;
        }

        public GameServerMessage buildPlayResponseCommand() {
            instance.command = GameServer.CMD_PLAY_RESPONSE;
            instance.payload = String.format("%s|%s|%s|%s",
                    instance.playerName,
                    instance.opponentName,
                    instance.message,
                    instance.countOfPlayerReceivedMessage
            );

            return instance;
        }

        public GameServerMessage build() {
            return instance;
        }

    }
}
