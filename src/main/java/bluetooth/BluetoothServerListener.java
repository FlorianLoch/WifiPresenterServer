package bluetooth;

/**
 * Created by florian on 12.03.15.
 */
public interface BluetoothServerListener {

    public void onConnected(BluetoothConnection conn);

    public void onReady();

    public void onError(Exception e);

    public void onHandshakeFailed();
}
