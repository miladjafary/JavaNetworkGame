package com.miladjafari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Thread {
    private static final Logger LOG = Logger.getLogger("Connection");

    private String host;
    private Integer localPort;
    private Integer port;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Consumer<String> onMessage = message -> LOG.info(String.format("Received: [%s]", message));
    private Consumer<Exception> onError = exception -> LOG.severe(String.format("Error on receiving message[%s]", exception.getMessage()));

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

    private void init() {
        try {
            host = socket.getInetAddress().getHostAddress();
            port = socket.getPort();
            localPort = socket.getLocalPort();

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error on initializing connection ", e);
        }
    }

    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                onMessage.accept(message);
                if (".".equals(message)) {
                    out.println("good bye");
                }
                out.println(message);
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error on listing to messages ", e);
        }
    }

    public String sendMessage(String message) {
        out.println(message);
        String response = null;
        try {
            response = in.readLine();
        } catch (IOException e) {
            onError.accept(e);
        }

        return response;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void onMessage(Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    public void onError(Consumer<Exception> onError) {
        this.onError = onError;
    }
}
