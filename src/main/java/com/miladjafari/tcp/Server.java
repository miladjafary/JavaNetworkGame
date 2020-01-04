package com.miladjafari.tcp;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);

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
                onConnection.accept(connection);
            }
        } catch (SocketException e) {
            logger.info(String.format("Server has been stopped on port[%s]", port));
        } catch (IOException e) {
            logger.error(String.format("Error in starting the server on port [%s]", port), e);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("An error has been occurred in stopping the server ", e);
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
