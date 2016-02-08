package maxime.macari.channelmessaging;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChannelListActivity extends Activity implements RequestListener, AdapterView.OnItemClickListener {
    ListView lvChannels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_channel_list);

        lvChannels = (ListView) findViewById(R.id.lvChannels);

        String method = "getchannels";
        SharedPreferences sharedPref = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken",null);

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        ConnexionAsync conn = new ConnexionAsync(method, params);
        conn.setRequestListener(this);
        conn.execute();


    }
    @Override
    public void onError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(String response) {

        try {
            JSONObject json = new JSONObject(response);

            JSONArray channels = json.getJSONArray("channels");

            final ArrayList<Channel> Chas = new ArrayList<>();

            for(int i = 0; i < channels.length(); i++){
                JSONObject channel = channels.getJSONObject(i);
                int channel_ID = Integer.parseInt(channel.getString("channelID").toString());
                int nbPersoCo = Integer.parseInt(channel.getString("connectedusers").toString());
                String name = channel.getString("name").toString();
                Channel cha = new Channel(channel_ID,name,nbPersoCo);
                Chas.add(cha);
            }


            ChannelAdapteur adapter = new ChannelAdapteur(this, Chas);
            lvChannels.setAdapter(adapter);
            lvChannels.setOnItemClickListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Intent intent = new Intent(ChannelListActivity.this, ChannelActivity.class);
        Channel oneChannel = (Channel)view.getTag();
        intent.putExtra("channelID", oneChannel.mChannelID);
        startActivity(intent);
    }

}
