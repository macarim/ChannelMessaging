package maxime.macari.channelmessaging.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import maxime.macari.channelmessaging.AudioRecord;
import maxime.macari.channelmessaging.R;
import maxime.macari.channelmessaging.UploadFileToServer;

/**
 * Created by Maxime on 14/03/2016.
 */
public class SoundRecordDialog extends DialogFragment implements View.OnClickListener, UploadFileToServer.OnUploadFileListener {
    boolean startStop = false;
    private AudioRecord audio;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        audio = new AudioRecord();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v =inflater.inflate(R.layout.alert_dialog_layout, null);
        builder.setView(v)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });
        // Create the AlertDialog object and return it
        Button btnEnregistrer = (Button) v.findViewById(R.id.btnEnregistrer);
        btnEnregistrer.setOnClickListener(this);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        Button btnEnregistrer = (Button) v.findViewById(R.id.btnEnregistrer);

        if (!startStop) {
            startStop = true;
            btnEnregistrer.setText("Stop");
            audio.startRecording();
        } else if (startStop) {
            startStop = false;
            btnEnregistrer.setText("Enregistrer");
            audio.stopRecording();
            uploadSound();
        }
    }



    public void uploadSound(){
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        String filePath = mFileName;
        File file = new File(filePath);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("channelid", String.valueOf(ChannelFragment.channel_ID)));
        params.add(new BasicNameValuePair("accesstoken", ChannelFragment.accessToken));
        UploadFileToServer conn = new UploadFileToServer(getContext(),filePath,params,this);
        conn.execute();
    }

    @Override
    public void onResponse(String result) {
        Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailed(IOException error) {

    }
}
