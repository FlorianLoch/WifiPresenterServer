package socket;

/**
 * Created by florian on 12.03.15.
 */
public interface ServerSocketListener {

    default public void onError(Exception e) {
        e.printStackTrace();
    }

    public void onHandshakeSuccessful();

    public void onHandshakeFailed();

    public void onClosed();

    public void onConnect(Connection conn);

}
