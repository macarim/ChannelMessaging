package maxime.macari.channelmessaging;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Maxime on 15/03/2016.
 */
public class AudioPlay {
    private MediaPlayer   mPlayer = null;
    private String mFileName = null;
    private static final String LOG_TAG = "AudioPlay";

    public MediaPlayer getmPlayer() {
        return mPlayer;
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public AudioPlay(){
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }
}
