package com.miladjafari;

import java.util.Optional;

public class GameServerRunner {
    private static final Integer DEFAULT_PORT = 4444;

    private static final String ACTION_TYPE_USER_USER_ENTERED_PORT = "user_entered_port";
    private static final String ACTION_TYPE_USE_DEFAULT_PORT = "use_default_port";
    private static final String ACTION_TYPE_UNKNOWN = "unknown";

    private void startServer(Integer port) {
        GameServer gameServer = new GameServer();
        gameServer.start(port);
    }

    private static void showHelp() {
        String help = "Usage: game-server.jar [PORT]\n";
        help = help.concat("Example:\n");
        help = help.concat("  game-server.jar 4444\n");

        System.out.println(help);
    }

    private boolean isPort(String argument) {
        return argument.matches("[0-9]+");
    }

    private String recognizeAction(String[] args) {
        String actionType = ACTION_TYPE_USE_DEFAULT_PORT;
        if (args.length > 0) {
            String argument = Optional.ofNullable(args[0]).orElse("").trim();

            if (argument.equals(ACTION_TYPE_USER_USER_ENTERED_PORT)) {
                actionType = ACTION_TYPE_USER_USER_ENTERED_PORT;
            } else if (argument.isEmpty()) {
                actionType = ACTION_TYPE_USE_DEFAULT_PORT;
            } else if (isPort(argument)) {
                actionType = ACTION_TYPE_USER_USER_ENTERED_PORT;
            } else {
                actionType = ACTION_TYPE_UNKNOWN;
            }
        }

        return actionType;
    }

    private void showInvalidOptionMessage() {
        System.out.println("Invalid option.");
        showHelp();
    }

    public void run(String[] args) {
        String action = recognizeAction(args);
        switch (action) {
            case ACTION_TYPE_UNKNOWN:
                showInvalidOptionMessage();
                break;
            case ACTION_TYPE_USE_DEFAULT_PORT:
                startServer(DEFAULT_PORT);
                break;
            case ACTION_TYPE_USER_USER_ENTERED_PORT:
                startServer(Integer.parseInt(args[0]));
                break;
            default:
                showHelp();
        }
    }

    public static void main(String[] args) {
        GameServerRunner runner = new GameServerRunner();
        runner.run(args);
    }
}
