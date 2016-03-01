package maxime.macari.channelmessaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import maxime.macari.channelmessaging.fragment.ChannelFragment;
import maxime.macari.channelmessaging.fragment.ChannelListFragment;

public class ChannelListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
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
        }
    }

}
