package maxime.macari.channelmessaging;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.IOException;

/**
 * Created by Maxime on 14/03/2016.
 */
public class AudioRecord
{
    private static final String LOG_TAG = "AudioRecord";
    private String mFileName = null;

    private MediaRecorder mRecorder;

   public void startRecording() {
       mRecorder = new MediaRecorder();
       mRecorder.reset();
       mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
           @Override
           public void onError(MediaRecorder mr, int what, int extra) {
               String s = "";
           }
       });
       mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
           @Override
           public void onInfo(MediaRecorder mr, int what, int extra) {
               String s = "";
           }
       });
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

   public String stopRecording() {
       try {
        mRecorder.stop();
       } catch (IllegalStateException e) {
           Log.e(LOG_TAG, "prepare() failed");
       }
        mRecorder.release();
        mRecorder = null;
       return mFileName;

    }

    public AudioRecord() {
        mFileName =Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }


}


