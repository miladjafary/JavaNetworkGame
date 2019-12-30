package com.miladjafari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = Logger.getLogger("Client");

    private Connection connection;

    public Client() {

    }

    public void connect(String host, Integer port) {
        try {
            logger.info(String.format("Connection to [%s][%s]...", host, port));
            connection = new Connection(new Socket(host, port));
        } catch (IOException exception) {
            logger.log(Level.SEVERE,
                    String.format("Error in connection to the server on [%s][%d]", host, port),
                    exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
