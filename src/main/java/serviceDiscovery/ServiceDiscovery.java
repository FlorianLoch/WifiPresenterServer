package serviceDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by florian on 13.06.15.
 */
public class ServiceDiscovery extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);
    public static final String DISCOVERY_REQUEST = "BluePresenterServer_Discovery_Request";
    public static final byte[] DISCOVERY_RESPONSE = ("BluePresenterServer_Discovery_Response").getBytes();

    private int port;

    public void makeDiscoverable(int port) {
        this.port = port;
        this.start();
    }

    @Override
    public void run() {
        try {
            log.info("ServiceDiscovery-Thread started!");

            DatagramSocket socket = new DatagramSocket(this.port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            log.info(String.format("ServiceDiscovery listening on port %d", port));

            while (true) {
                byte[] recvBuf = new byte[DISCOVERY_REQUEST.length() * 4]; //It might be UTF-32 in worst case, so 4 bytes per character
                DatagramPacket requestPacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(requestPacket);

                String content = new String(requestPacket.getData()).trim();

                log.info(String.format("Received packet from %s:%d", requestPacket.getAddress(), requestPacket.getPort()));
                log.info(String.format("Contains: '%s' (Length: %d)", content, content.length()));

                if (content.equals(DISCOVERY_REQUEST)) {
                    DatagramPacket responsePacket = new DatagramPacket(DISCOVERY_RESPONSE, DISCOVERY_RESPONSE.length, requestPacket.getAddress(), requestPacket.getPort());
                    socket.send(responsePacket);

                    log.info("Sent response packet to " + requestPacket.getAddress() + ":" + requestPacket.getPort());
                }
            }
        } catch (Exception e) {
            log.error("ServiceDiscovery-Thread crashed!", e);
        }
    }
}
