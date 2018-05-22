package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        String unm = sp1.getString("Unm", null);
        String pass = sp1.getString("Psw", null);
        ip = sp1.getString("ip", null);
        port = sp1.getString("port", null);
        username = sp1.getString("Unm", null);
        password = sp1.getString("Psw", null);
        System.out.println("Login Window!");
    }

    public void loginClick(View v) {
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed = sp.edit();
        Ed.putString("Unm", "" + R.id.username);
        Ed.putString("Psw", "" + R.id.password);
        Ed.putString("ip", "" + R.id.ip);
        Ed.putString("port", "" + R.id.port);
        Ed.commit();
        new UserLogin(ip, port, username, password);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
