package maxime.macari.channelmessaging;

import android.graphics.Bitmap;

/**
 * Created by Maxime on 02/02/2016.
 */
public interface RequestListenerImage {
    public void onError(Bitmap error);
    public void onCompleted(Bitmap response);
}
