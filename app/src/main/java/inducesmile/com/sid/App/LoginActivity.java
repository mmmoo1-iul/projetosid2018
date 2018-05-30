package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;

import java.util.HashMap;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;

//Esta aplicação serve como base para vos ajudar, precisam de completar os métodos To do de modo a que a aplicação faça o minimo que é suposto, podem adicionar novas features ou mudar a UI se acharem relevante.
@SuppressWarnings("all")
public class LoginActivity extends AppCompatActivity {
    private String ip, port, username, password, stringResult;
    private LoginActivity instance;
    private AsyncTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stringResult = "START";
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

        instance = this;
        if (task != null)
            task.cancel(true);
    }

    public void loginClick(View v) {
        if (task != null)
            task.cancel(true);
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
        final String checkLogin = "http://" + ip + ":" + port + "/sid/checkLogin.php";


        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if (!task.isCancelled() &&!stringResult.equals("WORKED")) {
                    AsyncTask otherTask = null;
                    HashMap<String, String> params = new HashMap<>();
                    params.put("uid", username);
                    params.put("pwd", password);
                    params.put("db", "HumidadeTemperatura");
                    stringResult = ConnectionHandler.getStringFromURL(checkLogin, params);
                    if (stringResult != null && stringResult.equals("WORKED")) {
                        Intent i = new Intent(instance, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        stringResult = "FAILED";
                        task.cancel(true);
                        task = null;
                    }
                }
                return null;
            }
        }.execute();
    }

}