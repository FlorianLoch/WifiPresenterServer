package bluetooth;

/**
 * Created by florian on 11.03.15.
 */
public interface BluetoothConnectionListener {

    public void onError(Exception e);

    public void onDisconnect();

    public void onMessage(String msg);

}
