package com.miladjafari;

import com.miladjafari.tcp.Connection;
import com.miladjafari.tcp.Server;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.miladjafari.GameServerMessage.*;

public class GameServer {
    private static final Logger logger = Logger.getLogger("GameServer");

    private Server server;
    private Map<String, Connection> playersConnection = new ConcurrentHashMap<>();

    public Map<String, Connection> getPlayersConnection() {
        return playersConnection;
    }

    public void start(Integer port) {
        server = Server.builder()
                .port(port)
                .onConnection(connection -> {
                    logger.info(String.format("[%s][%s] Connected",
                            connection.getHost(),
                            connection.getPort())
                    );

                    connection.onMessage(message -> handleMessage(message, connection));
                    connection.onClose(this::removePlayer);
                })
                .build();
        server.start();
    }

    public void stop() {
        Optional.ofNullable(server).ifPresent(Server::stop);
    }

    private void handleMessage(String message, Connection connection) {
        GameServerMessage serverMessage = GameServerMessage.builder().serverMessage(message).build();


        switch (serverMessage.getCommand()) {
            case CMD_SIGN_UP_REQUEST:
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

    private void removePlayer(Connection playerClosedConnection) {
        Optional<Map.Entry<String, Connection>> foundPlayer = playersConnection.entrySet()
                .stream()
                .filter(player -> {
                    Connection playerConnection = player.getValue();
                    return playerConnection.getHost().equals(playerClosedConnection.getHost())
                            && playerConnection.getPort().equals(playerClosedConnection.getPort());
                })
                .findFirst();

        foundPlayer.ifPresent(player -> playersConnection.remove(player.getKey()));
        logger.info(
                String.format("[%s][%s] \"%s\" Connection has been Closed",
                        playerClosedConnection.getHost(),
                        playerClosedConnection.getPort(),
                        foundPlayer.map(Map.Entry::getKey).orElse("?")
                )
        );
    }

    private void signUpPlayer(String playerName, Connection playerConnection) {
        GameServerMessage.Builder singUpResponseBuilder = GameServerMessage.builder().playerName(playerName);
        logger.debug(String.format("SingUp [%s]", playerName));

        if (!playersConnection.containsKey(playerName)) {
            playersConnection.put(playerName, playerConnection);

            singUpResponseBuilder.message(RESULT_SING_UP_SUCCESS);
            singUpResponseBuilder.players(playersConnection.keySet());

        } else {
            singUpResponseBuilder.message(RESULT_SING_UP_PLAYER_EXIST);
        }

        String singUpResponse = singUpResponseBuilder.buildSignUpResponseCommand().encodeToString();
        playerConnection.sendMessage(singUpResponse);
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
        logger.debug(String.format("Received [%s]: [%s]", playerName, serverMessage.encodeToString()));
    }

    private void logSendMessage(String playerName, GameServerMessage serverMessage) {
        logger.debug(String.format("Send     [%s]: [%s]", playerName, serverMessage.encodeToString()));
    }
}
