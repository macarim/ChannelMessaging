package maxime.macari.channelmessaging.fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import maxime.macari.channelmessaging.LoginActivity;
import maxime.macari.channelmessaging.Message;
import maxime.macari.channelmessaging.MessageAdapteur;
import maxime.macari.channelmessaging.NetworkManager;
import maxime.macari.channelmessaging.R;
import maxime.macari.channelmessaging.RequestListener;

public class ChannelFragment extends Fragment implements RequestListener, View.OnClickListener{

    private static final int REQUEST_WRITE_STORAGE = 112;
    private Handler handler;
    public int channel_ID;
    public String typeConnect;
    String accessToken;

    ListView lvMessages;
    EditText etNewMessage;
    Button btnEnvoyer;
    private ArrayList<Message> mCurrentListMessages;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.channel_fragment,container);

        SharedPreferences sharedPref = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        accessToken = sharedPref.getString("accessToken", null);

        lvMessages = (ListView)v.findViewById(R.id.lvMessages);
        etNewMessage = (EditText) v.findViewById(R.id.etNewMessage);
        btnEnvoyer = (Button) v.findViewById(R.id.btnEnvoyer);

        btnEnvoyer.setOnClickListener(this);

        channel_ID = getActivity().getIntent().getIntExtra("channelID",0);

        refreshMessage();
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                typeConnect = "Message";
                refreshMessage();
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(r);

        return v;
    }

    public void refreshMessage(){
        String method = "getmessages";

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();


    }


    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(String response) {

        if(typeConnect == "Message") {
            try {
                final ArrayList<Message> listMessage = new ArrayList<>();
                JSONObject json = new JSONObject(response);
                JSONArray messages = json.getJSONArray("messages");

                for (int i = 0; i < messages.length(); i++) {
                    JSONObject message = messages.getJSONObject(i);
                    int userID = Integer.parseInt(message.getString("userID").toString());
                    String messageTxt = message.getString("message").toString();
                    Date date = ConvertToDate(message.getString("date").toString());
                    String imageURL = message.getString("imageUrl").toString();
                    String userName = message.getString("username").toString();

                    Message msg = new Message(userID, messageTxt, date, imageURL, userName);
                    listMessage.add(msg);
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showAdapter(listMessage);
                } else {
                    this.mCurrentListMessages = listMessage;
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                JSONObject json = new JSONObject(response);
                int code = Integer.parseInt(json.getString("code").toString());

                if(code == 200){
                    Toast.makeText(getContext(), "message Envoyer", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAdapter(List<Message> listMessage) {
        MessageAdapteur adapter = new MessageAdapteur(getContext(), listMessage);
        lvMessages.setAdapter(adapter);
    }

    private Date ConvertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public void sendMessage(){
        String method = "sendmessage";
        SharedPreferences sharedPref = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken", null);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        params.put("message", etNewMessage.getText().toString());
        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAdapter(mCurrentListMessages);
                    mCurrentListMessages = null;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        typeConnect = "send";
        sendMessage();
        etNewMessage.setText("");
    }
}