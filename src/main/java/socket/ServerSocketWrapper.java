package socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by florian on 12.03.15.
 */
public class ServerSocketWrapper implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ServerSocketWrapper.class);

    private ServerSocket s;
    private ServerSocketListener listener;
    private final String passphrase;
    private boolean stop;

    public ServerSocketWrapper(int port, String passphrase) throws IOException {
        this(new ServerSocket(port), passphrase);
    }

    public ServerSocketWrapper(ServerSocket socket, String passphrase) {
        this.s = socket;
        this.passphrase = passphrase;
        this.stop = false;
    }

    public void setListener(ServerSocketListener l) {
        this.listener = l;
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                log.debug("Start listening on port " + this.s.getLocalPort() + " on interface " + this.s.getLocalSocketAddress());
                Socket socket = this.s.accept();

                if (null == socket || socket.isClosed()) {
                    continue;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                boolean handshakeSuccessful = false;
                try {
                    handshakeSuccessful = shakeHands(in, out);
                }
                catch (Exception ex) {
                    log.debug("Error caught during handshaking!", ex);
                }

                if (handshakeSuccessful) {
                    this.fireOnHandshakeSuccessful();

                    out.println("hs successful");
                    out.flush();

                    Connection conn = new Connection(in, out, socket);
                    this.fireOnConnected(conn);

                    break;
                }

                this.fireOnHandshakeFailed();
                out.println("hs failed");
                out.flush();
                in.close();
                out.close();
                socket.close();
            }

            log.debug("Shutdown server socket; stopped listening");

            this.fireOnClosed();

            //this.s.close();
        }
        catch (Exception e) {
            this.fireOnError(e);
        }
    }

    private boolean shakeHands(BufferedReader in, PrintWriter out) throws Exception {
        log.debug("Started handshake procedure");

        String nonce = generateNonce();

        log.debug("Generated nonce: " + nonce);
        log.debug("Passphrase is: " + this.passphrase);

        out.println("hello" + nonce);
        out.flush();

        String answer = in.readLine();

        if (null == answer) {
            throw new IllegalStateException("Answer received from client during handshake was null!");
        }

        String expectation = Hasher.computeHash(nonce + this.passphrase);

        log.debug("Expected hash: " + expectation + " (Length: " + expectation.length() + ")");
        log.debug("Received hash: " + answer + " (Length: " + answer.length() + ")");

        return expectation.equals(answer);
    }

    private static String generateNonce() {
        return "SECRET";
    }

    private void fireOnError(Exception e) {
        this.listener.onError(e);
    }

    private void fireOnHandshakeSuccessful() {
        this.listener.onHandshakeSuccessful();
    }

    private void fireOnHandshakeFailed() {
        this.listener.onHandshakeFailed();
    }

    private void fireOnClosed() {
        this.listener.onClosed();
    }

    private void fireOnConnected(Connection conn) {
        this.listener.onConnect(conn);
    }
}
