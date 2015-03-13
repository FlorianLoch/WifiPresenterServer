package bluetooth;

import javax.microedition.io.StreamConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by florian on 12.03.15.
 */
public class BluetoothConnection extends Thread {
    private ArrayList<BluetoothConnectionListener> listener;
    private StreamConnection conn;
    private BufferedReader in;
    private PrintWriter out;

    private boolean stop;

    public BluetoothConnection(StreamConnection conn) throws IOException {
        this(conn, new BufferedReader(new InputStreamReader(conn.openInputStream())), new PrintWriter(conn.openOutputStream()));
    }

    public BluetoothConnection(StreamConnection conn, BufferedReader in, PrintWriter out) {
        this.listener = new ArrayList<BluetoothConnectionListener>();

        this.conn = conn;
        this.in = in;
        this.out = out;

        this.stop = false;
    }


    @Override
    public synchronized void start() {
        this.stop = false;
        super.start();
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                String line = this.in.readLine();

                if (stop) {
                    this.disconnect();
                    break;
                }

                this.fireOnMessage(line);
            } catch (IOException e) {
                this.fireOnError(e);
            }
        }
    }

    public void halt() {
        this.stop = true;
    }

    public void disconnect() throws IOException {
        this.halt();
        this.fireOnDisconnect();

        this.in.close();
        this.out.close();
        this.conn.close();
    }

    public void addListener(BluetoothConnectionListener l) {
        this.listener.add(l);
    }

    private void fireOnDisconnect() {
        for (BluetoothConnectionListener l : this.listener) {
            l.onDisconnect();
        }
    }

    private void fireOnError(Exception e) {
        for (BluetoothConnectionListener l : this.listener) {
            l.onError(e);
        }
    }

    private void fireOnMessage(String msg) {
        for (BluetoothConnectionListener l : this.listener) {
            l.onMessage(msg);
        }
    }

}
