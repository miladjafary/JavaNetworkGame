package com.miladjafari;

public class Player {
    private Client client;

    private Integer counter = 0;

    public Player() {
        client = new Client();
        client.connect("localhost", 4444);

        client.getConnection().onMessage(message -> {
            String[] playerAndMessage = message.split("\\|");
            String playerName = playerAndMessage[0];
            String playerMessage = playerAndMessage[1];

            counter++;
        });
    }
}
