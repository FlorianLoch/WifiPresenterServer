package core;

import core.commands.LeftKeyPressCommand;
import core.commands.RightKeyPressCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceDiscovery.ServiceDiscovery;
import socket.Connection;
import socket.ConnectionListener;
import socket.ServerSocketListener;

import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by florian on 12.03.15.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8081;
    private static final String PASSPHRASE = "TOP_SECRET";

    private static final RemoteCommandDispatcher commandDispatcher;

    static {
        commandDispatcher = new RemoteCommandDispatcher();
        commandDispatcher.addCommand(new LeftKeyPressCommand());
        commandDispatcher.addCommand(new RightKeyPressCommand());
    }

    private TrayIcon trayIcon;
    private Server server;
    private ServiceDiscovery serviceDiscovery;

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
        this.serviceDiscovery = new ServiceDiscovery();
        this.serviceDiscovery.makeDiscoverable(PORT);
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
                conn.setListener(new ConnectionListener() {
                    @Override
                    public void onMessage(String msg) {
                        commandDispatcher.onMessage(msg);
                    }

                    @Override
                    public void onDisconnect() {
                        //Restart ServerSocket
                        server.startListeningForConnection();
                    }
                });
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

        PopupMenu pMenu = new PopupMenu();
        MenuItem mI_enterPairingMode = new MenuItem("Enter pairing mode");
        mI_enterPairingMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.this.server.startListeningForConnection();
            }
        });

        MenuItem mI_quit = new MenuItem("Quit");
        mI_quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        pMenu.add(mI_enterPairingMode);
        pMenu.add(mI_quit);

        this.trayIcon.setPopupMenu(pMenu);

        SystemTray.getSystemTray().add(this.trayIcon);
    }
}
