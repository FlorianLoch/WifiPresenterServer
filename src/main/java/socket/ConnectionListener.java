package socket;

/**
 * Created by florian on 12.03.15.
 */
public interface ConnectionListener {

    public void onMessage(String msg);

    default public void onError(Exception e) {
        e.printStackTrace();
    }

    public void onDisconnect();
}
