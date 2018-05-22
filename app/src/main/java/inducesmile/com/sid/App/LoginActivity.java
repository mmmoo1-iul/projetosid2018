package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.putString("Unm", ((EditText) (findViewById(R.id.username))).getText().toString());
        Ed.putString("Psw", ((EditText) (findViewById(R.id.password))).getText().toString());
        Ed.putString("ip", ((EditText) (findViewById(R.id.ip))).getText().toString());
        Ed.putString("port", ((EditText) (findViewById(R.id.port))).getText().toString());
        Ed.apply();
        new UserLogin(ip, port, username, password);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
