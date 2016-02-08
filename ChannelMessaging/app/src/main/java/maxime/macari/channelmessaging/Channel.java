package maxime.macari.channelmessaging;

/**
 * Created by Maxime on 08/02/2016.
 */
public class Channel {
    public int mChannelID;
    public String mName;
    public int mNbPersoConnected;

    public Channel(int channelID, String name, int nbPersoConnected){
        this.mChannelID = channelID;
        this.mName = name;
        this.mNbPersoConnected = nbPersoConnected;
    }

    public Channel(){

    }

    public int getChannelID() {
        return mChannelID;
    }

    public void setChannelID(int channelID) {
        mChannelID = channelID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getNbPersoConnected() {
        return mNbPersoConnected;
    }

    public void setNbPersoConnected(int nbPersoConnected) {
        mNbPersoConnected = nbPersoConnected;
    }
}
