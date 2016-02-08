package maxime.macari.channelmessaging;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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

public class ChannelActivity extends Activity implements RequestListener, View.OnClickListener{

    private Handler handler;
    public int channel_ID;
    public String typeCOnnect;

    ListView lvMessages;
    EditText etNewMessage;
    Button btnEnvoyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_channel);

        lvMessages = (ListView)findViewById(R.id.lvMessages);
        etNewMessage = (EditText) findViewById(R.id.etNewMessage);
        btnEnvoyer = (Button) findViewById(R.id.btnEnvoyer);

        btnEnvoyer.setOnClickListener(this);

        channel_ID = (int) getIntent().getSerializableExtra("channelID");

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                typeCOnnect = "Message";
                refreshMessage();

            }
        };

        handler.postDelayed(r, 1000);

    }

    public void refreshMessage(){
        String method = "getmessages";
        SharedPreferences sharedPref = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken",null);

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        ConnexionAsync conn = new ConnexionAsync(method, params);
        conn.setRequestListener(this);
        conn.execute();
    }


    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(String response) {

        if(typeCOnnect == "Message") {
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

                MessageAdapteur adapter = new MessageAdapteur(this, listMessage);
                lvMessages.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                JSONObject json = new JSONObject(response);
                int code = Integer.parseInt(json.getString("code").toString());

                if(code == 200){
                    Toast.makeText(ChannelActivity.this, "message Envoyer", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Date ConvertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
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
        SharedPreferences sharedPref = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken",null);

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        params.put("message", etNewMessage.getText().toString());
        ConnexionAsync conn = new ConnexionAsync(method, params);
        conn.setRequestListener(this);
        conn.execute();
    }

    @Override
    public void onClick(View v) {
        typeCOnnect = "send";
        sendMessage();
    }
}
