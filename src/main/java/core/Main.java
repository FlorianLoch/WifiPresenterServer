package core;

import core.commands.LeftKeyPressCommand;
import core.commands.RightKeyPressCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import socket.Connection;
import socket.ServerSocketListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by florian on 12.03.15.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 5555;
    private static final String PASSPHRASE = "TOP_SECRET";

    private static final RemoteCommandDispatcher commandDispatcher;

    static {
        commandDispatcher = new RemoteCommandDispatcher();
        commandDispatcher.addCommand(new LeftKeyPressCommand());
        commandDispatcher.addCommand(new RightKeyPressCommand());
    }

    private TrayIcon trayIcon;
    private Server server;

    public static void main(String[] args) {
        try {
            new Main();
        } catch (Exception e) {
            log.error("Could not instantiate main application. Execution aborted!", e);
            System.exit(0);
        }
    }

    public Main() throws IOException, AWTException {
        this.initTrayIcon();
        this.initServer();

    }

    private void initServer() throws IOException {
        this.server = new Server(PORT, PASSPHRASE);
        this.server.setListener(new ServerSocketListener() {
            @Override
            public void onHandshakeSuccessful() {

            }

            @Override
            public void onHandshakeFailed() {

            }

            @Override
            public void onClosed() {

            }

            @Override
            public void onConnect(Connection conn) {
                conn.setListener(commandDispatcher);
                conn.start();
            }
        });
        this.server.startListeningForConnection();
    }

    private void initTrayIcon() throws IOException, AWTException {
        InputStream iconInputStream = ClassLoader.getSystemResourceAsStream("tray_icon.png");
        Image imageForTrayIcon = ImageIO.read(iconInputStream);

        this.trayIcon = new TrayIcon(imageForTrayIcon);
        this.trayIcon.setImageAutoSize(true);

        SystemTray.getSystemTray().add(this.trayIcon);
    }
}
