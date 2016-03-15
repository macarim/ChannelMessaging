package maxime.macari.channelmessaging.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import maxime.macari.channelmessaging.Channel;
import maxime.macari.channelmessaging.ChannelAdapteur;
import maxime.macari.channelmessaging.ChannelListActivity;
import maxime.macari.channelmessaging.LoginActivity;
import maxime.macari.channelmessaging.NetworkManager;
import maxime.macari.channelmessaging.R;
import maxime.macari.channelmessaging.RequestListener;

public class ChannelListFragment extends Fragment implements RequestListener, View.OnClickListener {
    public ListView lvChannels;
    public ArrayList<Channel> Chas;
    Button friendsBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.channel_list_fragment,container);

        lvChannels = (ListView) v.findViewById(R.id.lvChannels);

        friendsBtn = (Button) v.findViewById(R.id.friendsBtn);
        friendsBtn.setOnClickListener(this);

        String method = "getchannels";
        SharedPreferences sharedPref =  getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken",null);

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();

        return v;
    }
    @Override
    public void onError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(String response) {

        try {
            JSONObject json = new JSONObject(response);

            JSONArray channels = json.getJSONArray("channels");

            Chas = new ArrayList<>();

            for(int i = 0; i < channels.length(); i++){
                JSONObject channel = channels.getJSONObject(i);
                int channel_ID = Integer.parseInt(channel.getString("channelID").toString());
                int nbPersoCo = Integer.parseInt(channel.getString("connectedusers").toString());
                String name = channel.getString("name").toString();
                Channel cha = new Channel(channel_ID,name,nbPersoCo);
                Chas.add(cha);
            }


            ChannelAdapteur adapter = new ChannelAdapteur(getContext(), Chas);
            lvChannels.setAdapter(adapter);
            lvChannels.setOnItemClickListener((ChannelListActivity)getActivity());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == friendsBtn.getId()){

        }
    }
}

