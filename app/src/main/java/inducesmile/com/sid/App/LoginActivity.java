package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;

//Esta aplicação serve como base para vos ajudar, precisam de completar os métodos To do de modo a que a aplicação faça o minimo que é suposto, podem adicionar novas features ou mudar a UI se acharem relevante.
public class LoginActivity extends AppCompatActivity {
    private String ip, port, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);
        ip = sp1.getString("ip", null);
        port = sp1.getString("port", null);
        username = sp1.getString("Unm", null);
        password = sp1.getString("Psw", null);
        ((EditText) (findViewById(R.id.username))).setText(username);
        ((EditText) (findViewById(R.id.password))).setText(password);
        ((EditText) (findViewById(R.id.ip))).setText(ip);
        ((EditText) (findViewById(R.id.port))).setText(port);
    }

    public void loginClick(View v) {
        username = ((EditText) (findViewById(R.id.username))).getText().toString();
        password = ((EditText) (findViewById(R.id.password))).getText().toString();
        ip = ((EditText) (findViewById(R.id.ip))).getText().toString();
        port = ((EditText) (findViewById(R.id.port))).getText().toString();
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.putString("Unm", username);
        Ed.putString("Psw", password);
        Ed.putString("ip", ip);
        Ed.putString("port", port);
        Ed.apply();

        new UserLogin(ip, port, username, password);
        final String checkLogin = "http://" + ip + ":" + port + "/migration.php?";    //+"uid=" + username + "&pwd=" + password;
        System.out.println("\n\n\n" + checkLogin + "\n\n\n");

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                HashMap<String, String> params = new HashMap<>();
                params.put("uid", username);
                params.put("pwd", password);
                params.put("db","HumidadeTemperatura");
                ConnectionHandler jParser = new ConnectionHandler();
                JSONArray loginConfirmation = jParser.getJSONFromUrl(checkLogin, params);
                return loginConfirmation;
            }
        }.execute();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}