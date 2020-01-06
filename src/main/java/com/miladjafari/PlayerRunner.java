package com.miladjafari;

import com.miladjafari.tcp.Client;
import com.miladjafari.tcp.Connection;

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
                player = Player.builder().name(playerName).connection(connection).build();
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
