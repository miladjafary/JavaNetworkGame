package com.miladjafari.tcp;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public class Connection {
    private static final Logger logger = Logger.getLogger("Connection");

    private String host = "";
    private Integer localPort = -1;
    private Integer port = -1;

    private Socket socket;
    private Reader reader;
    private PrintWriter writer;

    private Consumer<String> onMessage = message -> logger.info(String.format("Received: [%s]", message));
    private Consumer<Exception> onError = exception -> logger.error(String.format("Error on receiving message[%s]", exception.getMessage()));
    private Consumer<Connection> onClose = exception -> logger.info(String.format("[%s][%s] Connection has been closed", host, port));

    public Connection(Socket socket) {
        this.socket = socket;
        init();
    }

    public String getHost() {
        return host;
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public Integer getPort() {
        return port;
    }

    public void init() {
        try {
            host = socket.getInetAddress().getHostAddress();
            port = socket.getPort();
            localPort = socket.getLocalPort();

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new Reader(socket);
            reader.start();

        } catch (IOException e) {
            logger.error("Error on initializing connection ", e);
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
        onClose.accept(this);
    }

    public void onMessage(Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    public void onClose(Consumer<Connection> onClose) {
        this.onClose = onClose;
    }

    public void onError(Consumer<Exception> onError) {
        this.onError = onError;
    }

    private class Reader extends Thread {
        private BufferedReader reader;

        private void close() throws IOException {
            reader.close();
        }

        public Reader(Socket socket) {
            try {
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
            } catch (IOException e) {
                logger.error("Error on creating read thread", e);
            }
        }

        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    if (response != null) {
                        onMessage.accept(response);
                    }
                } catch (SocketException exception) {
                    onClose.accept(null);
                    break;
                } catch (IOException exception) {
                    logger.error("Error reading line", exception);
                    onError.accept(exception);

                    break;
                }
            }
        }
    }
}