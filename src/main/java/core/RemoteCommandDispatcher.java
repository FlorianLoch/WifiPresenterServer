package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socket.ConnectionListener;

import java.util.HashMap;

/**
 * Created by florian on 13.03.15.
 */
public class RemoteCommandDispatcher implements ConnectionListener {
    private static final Logger log = LoggerFactory.getLogger(RemoteCommandDispatcher.class);
    private HashMap<String, RemoteCommand> commands;

    public RemoteCommandDispatcher() {
        this.commands = new HashMap<>();
    }

    public void addCommand(RemoteCommand cmd) {
        if (this.commands.containsKey(cmd.getCommand().toLowerCase())) {
            throw new IllegalArgumentException("Command is already registered!");
        }

        this.commands.put(cmd.getCommand().toLowerCase(), cmd);
    }

    public void dispatchEvent(String command) {
        command = command.toLowerCase();

        RemoteCommand found = this.commands.get(command);

        if (null != found) {
            log.debug("Dispatching command '" + command + "'");
            found.onCommand();
            return;
        }

        log.debug("Could not dispatch command '" + command + "'");
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
