package maxime.macari.channelmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import maxime.macari.channelmessaging.fragment.ChannelFragment;
import maxime.macari.channelmessaging.fragment.ChannelListFragment;

public class ChannelListActivity extends GPSActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private MenuItem searchMenuItem;
    public ArrayList <Channel> listSearchChannels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Channel oneChannel = (Channel)view.getTag();
        ChannelListFragment fragA = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.ChannelListFragment);
        ChannelFragment fragB = (ChannelFragment)getSupportFragmentManager().findFragmentById(R.id.ChannelFragment);
        if(fragB == null|| !fragB.isInLayout()){
            Intent i = new Intent(this, ChannelActivity.class);

            i.putExtra("channelID", oneChannel.mChannelID);
            startActivity(i);
        } else {
            fragB.channel_ID = oneChannel.mChannelID;

            fragB.refreshMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchViewAction.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //TODO Handler
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        MenuItemCompat.collapseActionView(searchMenuItem);
//TODO Handler
        return false;
    }
    @Override
    public boolean onQueryTextChange(String s) {
        listSearchChannels.clear();
        ChannelListFragment fragA = (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.ChannelListFragment);
        ArrayList <Channel> tmpChannels = fragA.Chas;
        for(int i = 0; i < fragA.Chas.size(); i++){
            Channel cha = fragA.Chas.get(i);

            if(cha.getName().toUpperCase().indexOf(s.toUpperCase()) != -1 && s.length() != 0 && s != " "){
                listSearchChannels.add(cha);
            }
        }
        if(listSearchChannels.size() != 0 && listSearchChannels != null) {
            ChannelAdapteur adapter = new ChannelAdapteur(fragA.getContext(), listSearchChannels);
            fragA.lvChannels.setAdapter(adapter);
        } else {
            ChannelAdapteur adapter = new ChannelAdapteur(fragA.getContext(), tmpChannels);
            fragA.lvChannels.setAdapter(adapter);
        }

        return false;
    }

}

