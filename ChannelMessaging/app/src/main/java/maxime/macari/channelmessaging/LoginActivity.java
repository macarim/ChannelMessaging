package maxime.macari.channelmessaging;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, RequestListener {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private EditText etLogin;
    private EditText etMDP;
    private Button btnValider;
    public static final String PREFS_NAME = "PrefFile";

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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            login();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

    }

    private void login() {
        String method = "connect";


        HashMap<String, String> params = new HashMap<String,String>();
        params.put("username",etLogin.getText().toString());
        params.put("password", etMDP.getText().toString());
        NetworkManager conn = new NetworkManager(method, params);
        conn.setRequestListener(this);
        conn.execute();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(String response) {

        try {
            JSONObject json = new JSONObject(response);
            String accessToken = json.getString("accesstoken");

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("accessToken", accessToken);

            editor.commit();

            int code = Integer.parseInt(json.getString("code").toString());

            if(code == 200) {
                Intent I_News = new Intent(this, ChannelListActivity.class);
                this.startActivity(I_News);
            } else {
                onError(response);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "erreur de connexion", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login();
                }
            }
        }
    }


}

