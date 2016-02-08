package maxime.macari.channelmessaging;

import android.content.Entity;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class ConnexionAsync extends AsyncTask<String,Integer,String> {
    public String Method;
    public HashMap<String, String> Parametres;
    private static String URL = "http://www.raphaelbischof.fr/messaging/?function=";

    private ArrayList<RequestListener> listeners = new ArrayList<RequestListener>();

    public ConnexionAsync(String method,HashMap<String, String> parametres ) {
        this.Method = method;
        this.Parametres = parametres;
    }

    @Override
    protected String doInBackground(String... arg0) {
        String content = "";
        URL URI;

        try {
            URI = new URL(URL + Method);
            HttpURLConnection conn = (HttpURLConnection) URI.openConnection();
            conn.setReadTimeout(15000 );
            conn.setConnectTimeout(15000 );
            conn.setRequestMethod("POST" ) ;
            conn.setDoInput(true) ;
            conn.setDoOutput(true );
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString (Parametres));
            writer.flush();
            writer.close();
            os.close() ;
            int responseCode =conn.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader (new InputStreamReader(conn.getInputStream())) ;
                while ((line=br.readLine()) != null) {
                    content+=line ;
                }
            }
            else
            {
                content="";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return content;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        newWsRequestCompleted(result);
    }

    private String getPostDataString(HashMap<String, String> params) throws
            UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) first = false;
            else result.append("&") ;
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8")) ;
            result.append("=") ;
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString() ;
    }

    public void setRequestListener(RequestListener listener)
    {
        this.listeners.add(listener);
    }

    private void newWsRequestCompleted(String response){
        for(RequestListener oneListener : listeners){
            oneListener.onCompleted(response);
        }
    }
    private void newWsRequestError(String error){
        for(RequestListener oneListener : listeners){
            oneListener.onError(error);
        }
    }
}
