package maxime.macari.channelmessaging;

import org.w3c.dom.Text;

/**
 * Created by Maxime on 08/03/2016.
 */
public class User {
    public int mUserID;
    public Text mUsername;
    public Text mImageUrl;

    public User(){}

    public User(int userID, Text username, Text imageUrl){
        this.mUserID = userID;
        this.mUsername = username;
        this.mImageUrl = imageUrl;
    }

    public int getmUserID() {
        return mUserID;
    }

    public void setmUserID(int mUserID) {
        this.mUserID = mUserID;
    }

    public Text getmUsername() {
        return mUsername;
    }

    public void setmUsername(Text mUsername) {
        this.mUsername = mUsername;
    }

    public Text getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(Text mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
