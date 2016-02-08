package maxime.macari.channelmessaging;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener, RequestListener {
    private EditText etLogin;
    private EditText etMDP;
    private Button btnValider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        etLogin = (EditText)findViewById(R.id.etLogin);
        etMDP = (EditText) findViewById(R.id.etMdp);
        btnValider = (Button) findViewById(R.id.btnValider);

        btnValider.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String method = "connect";


        HashMap<String, String> params = new HashMap<String,String>();
        params.put("username",etLogin.getText().toString());
        params.put("password", etMDP.getText().toString());
        ConnexionAsync conn = new ConnexionAsync(method, params);
        conn.setRequestListener(this);
        conn.execute();

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(String response) {

        try {
            JSONObject json = new JSONObject(response);
            String accessTocken = json.getString("accesstoken");
            Toast.makeText(getApplicationContext(),accessTocken,Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

