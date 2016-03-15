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
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by Maxime on 08/02/2016.
 */
public class MessageAdapteur extends BaseAdapter {

    public Context mContext;
    public List<Message> mMessages;
    public String fileName;
    AudioPlay audioPlay = new AudioPlay();

    public MessageAdapteur(Context context, List<Message> messages) {
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
        View customView = null;
        if (mMessages.get(position).mMessageImageUrl.equals("") && mMessages.get(position).mSoundUrl.equals("")) {
            customView = inflater.inflate(R.layout.custom_list_item_image_layout, parent, false);
            TextView tvTitre = (TextView) customView.findViewById(R.id.tvTitre);
            final ImageView ivUser = (ImageView) customView.findViewById(R.id.ivUser);
            TextView tvNbConnect = (TextView) customView.findViewById(R.id.tvNbConnect);

            fileName = mMessages.get(position).mUserName;
            ivUser.setImageBitmap(null);
            Bitmap img = BitmapFactory.decodeFile(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
            if (img == null) {
                NetworkManagerImage conn = new NetworkManagerImage(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName, mMessages.get(position).mImageURL);
                conn.setRequestListener(new RequestListenerImage() {
                    @Override
                    public void onError(Bitmap error) {

                    }

                    @Override
                    public void onCompleted(Bitmap response) {
                        if (response != null)
                            response = getRoundedCornerBitmap(response, 50);

                        ivUser.setImageBitmap(response);
                    }
                });
                conn.execute();
            } else {
                if (img != null)
                    img = getRoundedCornerBitmap(img, 50);

                ivUser.setImageBitmap(img);
            }
            customView.setTag(mMessages.get(position));
            tvTitre.setText(mMessages.get(position).mMessage);
            tvNbConnect.setText(mMessages.get(position).mUserName);
            fileName = mMessages.get(position).mUserName;
        } else if (!mMessages.get(position).mMessageImageUrl.equals("") && mMessages.get(position).mSoundUrl.equals("")) {
            customView = inflater.inflate(R.layout.custom_list_item_send_image, parent, false);
            String fileName1 = mMessages.get(position).mMessageImageUrl.substring(47, 83);
            final ImageView ivSendPhoto = (ImageView) customView.findViewById(R.id.ivSendPhoto);
            TextView tvPseudo = (TextView) customView.findViewById(R.id.tvNbConnect);

            tvPseudo.setText(mMessages.get(position).mUserName);

            Bitmap img = BitmapFactory.decodeFile(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName1);
            if (img == null) {
                NetworkManagerImage conn = new NetworkManagerImage(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName1, mMessages.get(position).mMessageImageUrl);
                conn.setRequestListener(new RequestListenerImage() {
                    @Override
                    public void onError(Bitmap error) {

                    }

                    @Override
                    public void onCompleted(Bitmap response) {
                        if (response != null)
                            response = getRoundedCornerBitmap(response, 10);

                        ivSendPhoto.setImageBitmap(response);
                    }
                });
                conn.execute();
            } else {
                if (img != null)
                    img = getRoundedCornerBitmap(img, 50);

                ivSendPhoto.setImageBitmap(img);
            }

            final ImageView ivUser = (ImageView) customView.findViewById(R.id.ivUser);

            fileName = mMessages.get(position).mUserName;
            ivUser.setImageBitmap(null);
            Bitmap img1 = BitmapFactory.decodeFile(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
            if (img1 == null) {
                NetworkManagerImage conn = new NetworkManagerImage(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName, mMessages.get(position).mImageURL);
                conn.setRequestListener(new RequestListenerImage() {
                    @Override
                    public void onError(Bitmap error) {

                    }

                    @Override
                    public void onCompleted(Bitmap response) {
                        if (response != null)
                            response = getRoundedCornerBitmap(response, 50);

                        ivUser.setImageBitmap(response);
                    }
                });
                conn.execute();
            } else {
                img1 = getRoundedCornerBitmap(img1, 50);

                ivUser.setImageBitmap(img1);
            }

        } else {
            customView = inflater.inflate(R.layout.custom_list_item_sound_layout, parent, false);

            final ImageView ivUser = (ImageView) customView.findViewById(R.id.ivUser);
            ImageButton playImageBtn = (ImageButton) customView.findViewById(R.id.playImageBtn);
            final SeekBar soundPb = (SeekBar) customView.findViewById(R.id.soundPb);

            playImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    audioPlay.startPlaying();
                    MediaPlayer mMediaPlayer = audioPlay.getmPlayer();
                    final int duration = mMediaPlayer.getDuration();
                    final int amoungToupdate = duration / 100;
                    final Timer mTimer = new Timer();
                    final TimerTask tt = new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (!(amoungToupdate * soundPb.getProgress() >= duration)) {
                                        int p = soundPb.getProgress();
                                        p += 10;
                                        soundPb.setProgress(p);
                                    }
                                }
                            });
                            if ((amoungToupdate * soundPb.getProgress() >= duration)) {
                                mTimer.cancel();
                            }
                        };
                    };
                    mTimer.schedule(tt, 0,100);

                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {

                        }
                    });
                }

            });


            fileName = mMessages.get(position).mUserName;
            ivUser.setImageBitmap(null);
            Bitmap img1 = BitmapFactory.decodeFile(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
            if (img1 == null) {
                NetworkManagerImage conn = new NetworkManagerImage(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName, mMessages.get(position).mImageURL);
                conn.setRequestListener(new RequestListenerImage() {
                    @Override
                    public void onError(Bitmap error) {

                    }

                    @Override
                    public void onCompleted(Bitmap response) {
                        if (response != null)
                            response = getRoundedCornerBitmap(response, 50);

                        ivUser.setImageBitmap(response);
                    }
                });
                conn.execute();
            } else {
                img1 = getRoundedCornerBitmap(img1, 50);

                ivUser.setImageBitmap(img1);
            }

        }
        return customView;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundedValue) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = roundedValue;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
