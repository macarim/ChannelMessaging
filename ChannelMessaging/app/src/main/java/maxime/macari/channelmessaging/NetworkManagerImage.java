package maxime.macari.channelmessaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Maxime on 29/02/2016.
 */
public class NetworkManagerImage extends AsyncTask<Void,Void,Bitmap> {

    public String URL;
    public String fileName;

    private ArrayList<RequestListenerImage> listeners = new ArrayList<RequestListenerImage>();

    public NetworkManagerImage(String fileName, String URL) {
        this.fileName = fileName;
        this.URL = URL;
    }

    protected Bitmap doInBackground(Void... params) {
        Bitmap ima = null;
        try {
            URL url = new URL(URL);
            File file = new File(fileName);
            file.createNewFile();
            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();
             /* Define InputStreams to read from the URLConnection.*/
            InputStream is = ucon.getInputStream();
             /* Read bytes to the Buffer until there is nothing more to
                read(-1) and write on the fly in the file.*/
            FileOutputStream fos = new FileOutputStream(file);
            final int BUFFER_SIZE = 23 * 1024;
            BufferedInputStream bis = new BufferedInputStream(is,
                    BUFFER_SIZE);
            byte[] baf = new byte[BUFFER_SIZE];
            int actual = 0;
            while (actual != -1) {
                fos.write(baf, 0, actual);
                actual = bis.read(baf, 0, BUFFER_SIZE);
            }
            fos.close();
            return  ima;
        } catch (IOException e) {
            //TODO HANDLER
            newWsRequestError(null);
        }
        ima = BitmapFactory.decodeFile(fileName);
        return ima;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        super.onPostExecute(result);
        newWsRequestCompleted(result);
    }

    public void setRequestListener(RequestListenerImage listener)
    {
        this.listeners.add(listener);
    }

    private void newWsRequestCompleted(Bitmap response){
        for(RequestListenerImage oneListener : listeners){
            oneListener.onCompleted(response);
        }
    }
    private void newWsRequestError(Bitmap error){
        for(RequestListenerImage oneListener : listeners){
            oneListener.onError(error);
        }
    }

}
