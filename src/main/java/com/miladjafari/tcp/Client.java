package com.miladjafari.tcp;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class);

    private Connection connection;

    public Client() {

    }

    public void connect(String host, Integer port) {
        try {
            logger.info(String.format("Connecting to [%s][%s]...", host, port));
            connection = new Connection(new Socket(host, port));
        } catch (IOException exception) {
            logger.error(String.format("Error in connection to the server on [%s][%d]", host, port), exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
