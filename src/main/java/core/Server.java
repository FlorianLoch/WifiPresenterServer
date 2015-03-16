package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socket.Connection;
import socket.ServerSocketListener;
import socket.ServerSocketWrapper;

import java.io.IOException;

/**
 * Created by florian on 12.03.15.
 */
public class Server implements ServerSocketListener {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private ServerSocketWrapper socket;
    private ServerSocketListener listener;

    public Server(int port, String passphrase) throws IOException {
        this(new ServerSocketWrapper(port, passphrase));
    }

    public Server(ServerSocketWrapper socket) {
        socket.setListener(this);
        this.socket = socket;
    }

    public void setListener(ServerSocketListener l) {
        this.listener = l;
    }

    public void startListeningForConnection() {
        new Thread(this.socket).start();
        log.debug("TCP-core.Server started!");
    }

    @Override
    public void onHandshakeSuccessful() {
        log.debug("Handshake completed successfully!");

        if (this.listener != null) {
            this.listener.onHandshakeSuccessful();
        }
    }

    @Override
    public void onHandshakeFailed() {
        log.debug("Handshake failed!");

        if (this.listener != null) {
            this.listener.onHandshakeFailed();
        }
    }

    @Override
    public void onClosed() {
        log.debug("ServerSocket closed!");

        if (this.listener != null) {
            this.listener.onClosed();
        }
    }

    @Override
    public void onConnect(Connection conn) {
        log.debug("New connection established!");

        if (this.listener != null) {
            this.listener.onConnect(conn);
        }
    }

}
