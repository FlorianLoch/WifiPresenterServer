package core;

import socket.ConnectionListener;

import java.util.ArrayList;

/**
 * Created by florian on 13.03.15.
 */
public class RemoteCommandDispatcher implements ConnectionListener {
    private ArrayList<RemoteCommand> commands;

    public RemoteCommandDispatcher() {
        this.commands = new ArrayList<>();
    }

    public void addCommand(RemoteCommand cmd) {
        this.commands.add(cmd);
    }

    public void dispatchEvent(String command) {
        command = command.toLowerCase();

        for (RemoteCommand cmd : this.commands) {
            if (cmd.getCommand().toLowerCase().equals(command)) {
                cmd.onCommand();
                return;
            }
        }
    }

    @Override
    public void onMessage(String msg) {
        if (msg.startsWith("\\")) {
            int end = msg.indexOf(" ");
            if (-1 == end) {
                end = msg.length();
            }

            String command = msg.substring(1, end);

            this.dispatchEvent(command);
        }
    }

    @Override
    public void onDisconnect() {

    }
}
