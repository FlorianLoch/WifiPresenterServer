package bluetooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by florian on 11.03.15.
 */
public class BluetoothWrapper implements Runnable {
    private BluetoothServerListener listener;
    private final String passphrase;

    public BluetoothWrapper(String passphrase) {
        this.passphrase = passphrase;
    }

    public void setListener(BluetoothServerListener l) {
        this.listener = l;
    }

    @Override
    public void run() {
        try {
            StreamConnectionNotifier notifier = this.initBluetoothConnection();

            if (null == notifier) {
                this.fireOnError(new IllegalStateException("initBluetoothConnection() returned null!"));
                return;
            }

            this.fireOnReady();

            StreamConnection conn = notifier.acceptAndOpen();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.openInputStream()));
            PrintWriter out = new PrintWriter(conn.openOutputStream());

            boolean handShakeSuccessful = this.shakeHands(in, out);

            if (!handShakeSuccessful) {
                this.fireOnHandshakeFailed();
                return;
            }

            BluetoothConnection btConn = new BluetoothConnection(conn, in, out);

            this.fireOnConnect(btConn);
        }
        catch (Exception ex) {
            this.fireOnError(ex);
        }
    }

    private StreamConnectionNotifier initBluetoothConnection() throws Exception {
        UUID uuid = UUID.randomUUID();

        LocalDevice localDevice = LocalDevice.getLocalDevice();
        localDevice.setDiscoverable(DiscoveryAgent.GIAC);

        String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";

        StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(url);

        return notifier;
    }

    private boolean shakeHands(BufferedReader in, PrintWriter out) throws Exception {
        String nonce = generateNonce();

        out.println(nonce);
        out.flush();

        String answer = in.readLine();

        if (null == answer) {
            throw new IllegalStateException("Answer received from client during handshake was null!");
        }

        String expectation = computeHash(nonce + this.passphrase);

        return expectation.equals(answer);
    }

    private static String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(input.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();

        return new String(digest, "UTF-8");
    }

    private static String generateNonce() {
        return "SECRET";
    }

    private void fireOnConnect(BluetoothConnection conn) {
        listener.onConnected(conn);
    }

    private void fireOnError(Exception e) {
        listener.onError(e);
    }

    private void fireOnReady() {
        listener.onReady();
    }

    private void fireOnHandshakeFailed() {
        listener.onHandshakeFailed();
    }
}
