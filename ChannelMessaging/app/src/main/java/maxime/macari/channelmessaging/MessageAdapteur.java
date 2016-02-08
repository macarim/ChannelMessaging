package maxime.macari.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Maxime on 08/02/2016.
 */
public class MessageAdapteur extends BaseAdapter {

    public Context mContext;
    public List<Message> mMessages;

    public MessageAdapteur(Context context, List<Message> messages){
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).mUserID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_list_item_layout, parent, false);
        TextView tvTitre = (TextView) customView.findViewById(R.id.tvTitre);
        TextView tvNbConnecte = (TextView) customView.findViewById(R.id.tvNbConnecte);

        customView.setTag(mMessages.get(position));

        tvTitre.setText(mMessages.get(position).mMessage);
        tvNbConnecte.setText(mMessages.get(position).mUserName);
        return customView;
    }
}
