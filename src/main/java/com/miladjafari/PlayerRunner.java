package com.miladjafari;

import com.miladjafari.tcp.Client;
import com.miladjafari.tcp.Connection;

import java.util.Scanner;
import java.util.Set;

public class PlayerRunner {

    private Player player;

    private Connection connectToServer(String host, Integer port) {
        Client client = new Client();
        client.connect(host, port);
        return client.getConnection();
    }

    private static void showHelp() {
        String help = "Usage: player-client.jar PlayerName GameServerHost GameServerPort\n";
        help = help.concat("Example:\n");
        help = help.concat("  player-client.jar Milad 127.0.0.1 4444\n");

        System.out.println(help);
    }

    private void validatePort(String port) {
        if (!isPort(port)) {
            throw new IllegalArgumentException("GameServerPort must be only digits.");
        }
    }

    private boolean isPort(String argument) {
        return argument.matches("[0-9]+");
    }

    private void startTheGame(Set<String> players) {
        if (players.size() > 1) {
            String opponentPlayer = chooseOpponentPlayer(players);
            player.playGameWith(opponentPlayer, "Play with me");
        } else {
            System.out.println("You are the only player in GameServer. Please wait until other player choose you as an opponent.");
        }
    }

    private String chooseOpponentPlayer(Set<String> players) {
        System.out.println("Registered players in GameServer:");
        players.stream()
                .filter(playerName->!player.getName().equals(playerName))
                .forEach(player -> System.out.println("- " + player));

        while (true) {
            System.out.println("Enter your opponent playerName: ");
            Scanner inputReader = new Scanner(System.in);
            String opponent = inputReader.nextLine();
            if (players.contains(opponent)) {
                return opponent;
            } else {
                System.out.println(
                        String.format("[%s] player is not exist in GameServer.Please enter valid name", opponent));
            }
        }
    }

    private void onSingUpResponse(GameServerMessage singUpResponse) {
        if (singUpResponse.getMessage().equals(GameServerMessage.RESULT_SING_UP_SUCCESS)) {
            startTheGame(singUpResponse.getPlayers());
        } else {
            System.out.println("SingUpResult: " + singUpResponse.getMessage());
            throw new IllegalArgumentException(
                    String.format("%s as a player name is already exist in GameServer", player.getName()));
        }
    }

    private void run(String[] args) {
        if (args.length < 3 || args.length > 3) {
            showHelp();
        } else {

            String playerName = args[0];
            String host = args[1];
            String port = args[2];

            try {
                validatePort(port);

                Connection connection = connectToServer(host, Integer.parseInt(port));
                player = Player.builder()
                        .name(playerName)
                        .connection(connection)
                        .onSingUpResponse(this::onSingUpResponse)
                        .build();

                player.signUp();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        PlayerRunner runner = new PlayerRunner();
        runner.run(args);
    }
}
