package com.miladjafari;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger("Server");

    private Integer port;
    private ServerSocket serverSocket;

    private Consumer<Connection> onConnection = connection -> logger.info(
            String.format("Incoming Connection [%s][%s]", connection.getHost(), connection.getPort())
    );

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server is listing on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket);
                connection.start();

                onConnection.accept(connection);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in starting the server on port" + port, e);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error has been occurred in stopping the server ", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Server instance = new Server();

        public Builder port(Integer port) {
            instance.port = port;
            return this;
        }

        public Builder onConnection(Consumer<Connection> onConnection) {
            instance.onConnection = onConnection;
            return this;
        }

        public Server build() {
            return instance;
        }
    }
}
