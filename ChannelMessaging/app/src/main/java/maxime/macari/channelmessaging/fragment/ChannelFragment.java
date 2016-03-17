package maxime.macari.channelmessaging.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import maxime.macari.channelmessaging.GPSActivity;
import maxime.macari.channelmessaging.LoginActivity;
import maxime.macari.channelmessaging.MapActivity;
import maxime.macari.channelmessaging.Message;
import maxime.macari.channelmessaging.MessageAdapteur;
import maxime.macari.channelmessaging.NetworkManager;
import maxime.macari.channelmessaging.R;
import maxime.macari.channelmessaging.RequestListener;
import maxime.macari.channelmessaging.UploadFileToServer;

public class ChannelFragment extends Fragment implements RequestListener, View.OnClickListener, AdapterView.OnItemClickListener, UploadFileToServer.OnUploadFileListener{

    private Handler handler;
    public static int channel_ID;
    public String typeConnect;
    public static String accessToken;
    public static Context context = null;

    ListView lvMessages;
    EditText etNewMessage;
    ImageButton btnEnvoyer;
    ImageButton btnImage;
    ImageButton btnSound;
    Location mCurrentLocation = GPSActivity.getCurrentLocation();
    public static final int PICTURE_REQUEST_CODE = 0;
    public static final int REQUEST_WRITE_STORAGE = 10;
    public static final int REQUEST_RECORD_SOUND = 20;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.channel_fragment,container);
        context = getContext();

        SharedPreferences sharedPref = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        accessToken = sharedPref.getString("accessToken", null);

        lvMessages = (ListView)v.findViewById(R.id.lvMessages);
        etNewMessage = (EditText) v.findViewById(R.id.etNewMessage);
        btnEnvoyer = (ImageButton) v.findViewById(R.id.btnEnvoyer);
        btnImage = (ImageButton) v.findViewById(R.id.btnPhoto);
        btnSound = (ImageButton) v.findViewById(R.id.btnSound);

        btnEnvoyer.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnSound.setOnClickListener(this);

        channel_ID = getActivity().getIntent().getIntExtra("channelID",0);

        refreshMessage();
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                typeConnect = "Message";
                refreshMessage();
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(r);

        return v;
    }

    public void refreshMessage(){
        String method = "getmessages";

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();


    }


    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(String response) {

        if(typeConnect == "Message") {
            try {
                final ArrayList<Message> listMessage = new ArrayList<>();
                JSONObject json = new JSONObject(response);
                JSONArray messages = json.getJSONArray("messages");

                for (int i = 0; i < messages.length(); i++) {
                    JSONObject message = messages.getJSONObject(i);
                    int userID = Integer.parseInt(message.getString("userID").toString());
                    String messageTxt = message.getString("message").toString();
                    Date date = ConvertToDate(message.getString("date").toString());
                    String imageURL = message.getString("imageUrl").toString();
                    String userName = message.getString("username").toString();
                    double latitude = message.getDouble("latitude");
                    double longitude = message.getDouble("longitude");
                    String messageImageUrl = message.getString("messageImageUrl");
                    String soundUrl = message.getString("soundUrl");

                    Message msg = new Message(userID, messageTxt, date, imageURL, userName, latitude, longitude, messageImageUrl,soundUrl);
                    listMessage.add(msg);
                }

                showAdapter(listMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                JSONObject json = new JSONObject(response);
                int code = Integer.parseInt(json.getString("code").toString());

                if(code == 200){
                    Toast.makeText(getContext(), "message Envoyer", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAdapter(List<Message> listMessage) {
        int index = lvMessages.getFirstVisiblePosition();
        View v = lvMessages.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - lvMessages.getPaddingTop());

        MessageAdapteur adapter = new MessageAdapteur(getContext(), listMessage);
        lvMessages.setAdapter(adapter);
        lvMessages.setSelectionFromTop(index, top);
        lvMessages.setOnItemClickListener(this);
    }

    private Date ConvertToDate(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public void sendMessage(){
        String method = "sendmessage";
        SharedPreferences sharedPref = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String accessToken = sharedPref.getString("accessToken", null);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accesstoken", accessToken);
        params.put("channelid", String.valueOf(channel_ID));
        params.put("message", etNewMessage.getText().toString());

        if(String.valueOf(mCurrentLocation.getLongitude()) != null && String.valueOf(mCurrentLocation.getLatitude()) != null){
            params.put("latitude",String.valueOf(mCurrentLocation.getLatitude()));
            params.put("longitude",String.valueOf(mCurrentLocation.getLongitude()));
        }

        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnEnvoyer.getId()) {
            typeConnect = "send";
            sendMessage();
            etNewMessage.setText("");
        }else if(v.getId() == btnImage.getId()){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_WRITE_STORAGE);
                return;
            } else {
                takePhoto();
            }
        }
        else if(v.getId() == btnSound.getId()){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_SOUND);
                return;
            } else {
                registerSound();
            }

        }
    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Création de l’appel à l’application appareil photo pour récupérer une image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/photo"))); //Emplacement de l’image stockée
        startActivityForResult(intent, PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/photo";
        if(PICTURE_REQUEST_CODE == requestCode) {
            File file = new File(filePath);
            try {
                resizeFile(file, getContext());

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("channelid", String.valueOf(channel_ID)));
                params.add(new BasicNameValuePair("accesstoken", accessToken));
                UploadFileToServer conn = new UploadFileToServer(getContext(),filePath,params,this);
                conn.execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resizeFile(File f,Context context) throws IOException {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=400;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        int i = getCameraPhotoOrientation(context, Uri.fromFile(f), f.getAbsolutePath());
        if (o.outWidth>o.outHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(i); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        }
        try {
            f.delete();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        final Message msg = (Message) view.getTag();

        if(msg != null) {
            if(msg.mMessageImageUrl.equals("")) {
                CharSequence[] arr = new String[2];
                arr[0] = "Ajouter un ami";
                arr[1] = "Voir sur la carte";
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)//drawable de l'icone à gauche du titre
                        .setTitle(R.string.make_a_choice)//Titre de l'alert dialog
                        .setItems(arr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//which = la position de l'item appuyé
                                if (which == 0) {
                                    //Do some stuff (1st item touched)
                                } else {
                                    //Do some over stuff (2nd item touched)
                                    Intent i = new Intent(getContext(), MapActivity.class);

                                    i.putExtra("userName", msg.mUserName);
                                    i.putExtra("latitude", String.valueOf(msg.mLatitude));
                                    i.putExtra("longitude", String.valueOf(msg.mLongitude));
                                    startActivity(i);
                                }
                            }
                        })//items de l'alert dialog
                        .show();
            }
        }else{
            Toast.makeText(getContext(),"En cour de développement",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResponse(String result) {
        Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailed(IOException error) {
        Toast.makeText(getContext(), error.toString(),Toast.LENGTH_LONG).show();
    }

    public void registerSound() {
        SoundRecordDialog newFragment = new SoundRecordDialog();
        newFragment.show(getActivity().getSupportFragmentManager(), "missiles");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
            }
            case REQUEST_RECORD_SOUND: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerSound();
                }
            }
        }
    }
}