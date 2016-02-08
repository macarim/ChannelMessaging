package maxime.macari.channelmessaging;

/**
 * Created by Maxime on 02/02/2016.
 */
public interface RequestListener {
    public void onError(String error);
    public void onCompleted(String response);
}
