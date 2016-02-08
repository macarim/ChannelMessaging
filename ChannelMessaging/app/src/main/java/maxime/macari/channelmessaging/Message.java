package maxime.macari.channelmessaging;

import java.util.Date;

/**
 * Created by Maxime on 08/02/2016.
 */
public class Message {
    public int mUserID;
    public String mMessage;
    public Date mDate;
    public String mImageURL;
    public String mUserName;

    public Message(int userID, String message, Date date, String imageURL, String userName){
        this.mUserID = userID;
        this.mMessage = message;
        this.mDate = date;
        this.mImageURL = imageURL;
        this.mUserName = userName;
    }

}
