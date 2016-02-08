package maxime.macari.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Maxime on 08/02/2016.
 */
public class ChannelAdapteur extends BaseAdapter {

    public Context mContext;
    public List<Channel> mChannels;

    public ChannelAdapteur(Context context, List<Channel> channels){
        this.mContext = context;
        this.mChannels = channels;
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public Channel getItem(int position) {
        return mChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mChannels.get(position).mChannelID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_list_item_layout, parent, false);
        TextView tvTitre = (TextView) customView.findViewById(R.id.tvTitre);
        TextView tvNbConnecte = (TextView) customView.findViewById(R.id.tvNbConnecte);

        customView.setTag(mChannels.get(position));

        tvTitre.setText(mChannels.get(position).mName);
        tvNbConnecte.setText("Nombre d'utilisateurs connectés : "+mChannels.get(position).mNbPersoConnected);
        return customView;

    }
}
