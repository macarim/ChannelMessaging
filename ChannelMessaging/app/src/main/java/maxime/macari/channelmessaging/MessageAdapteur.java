package maxime.macari.channelmessaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Maxime on 08/02/2016.
 */
public class MessageAdapteur extends BaseAdapter {

    public Context mContext;
    public List<Message> mMessages;
    public String fileName;

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
        View customView = inflater.inflate(R.layout.custom_list_item_image_layout, parent, false);
        TextView tvTitre = (TextView) customView.findViewById(R.id.tvTitre);
        final ImageView ivUser = (ImageView) customView.findViewById(R.id.ivUser);
        TextView tvNbConnect = (TextView) customView.findViewById(R.id.tvNbConnect);

        fileName = mMessages.get(position).mUserName;
        ivUser.setImageBitmap(null);
        Bitmap img = BitmapFactory.decodeFile(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+fileName);
        if(img == null) {
            NetworkManagerImage conn = new NetworkManagerImage(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName, mMessages.get(position).mImageURL);
            conn.setRequestListener(new RequestListenerImage() {
                @Override
                public void onError(Bitmap error) {

                }

                @Override
                public void onCompleted(Bitmap response) {
                    if(response != null)
                    response = getRoundedCornerBitmap(response);

                    ivUser.setImageBitmap(response);
                }
            });
            conn.execute();
        } else{
            if(img != null)
            img = getRoundedCornerBitmap(img);

            ivUser.setImageBitmap(img);
        }
        customView.setTag(mMessages.get(position));
        tvTitre.setText(mMessages.get(position).mMessage);
        tvNbConnect.setText(mMessages.get(position).mUserName);
        fileName = mMessages.get(position).mUserName;

        return customView;
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
