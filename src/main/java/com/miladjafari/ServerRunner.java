package com.miladjafari;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerRunner {
    private static final Logger logger = Logger.getLogger("ServerRunner");

    private Map<String, Player> players = new HashMap<>();
    public static void main(String[] args) {
        Server server = Server.builder()
                .port(4444)
                .onConnection(connection -> {
                    connection.onMessage(message -> {
                        logger.info(String.format("Hi Received [%s]",message));
                    });

                    logger.info(String.format("Hi Incoming connection [%s][%s]",
                            connection.getHost(),
                            connection.getPort())
                    );
                })

                .build();

        server.start();
    }
}
