package inducesmile.com.sid.App;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import inducesmile.com.sid.Connection.ConnectionHandler;
import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.Helper.UserLogin;
import inducesmile.com.sid.R;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username = UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    DataBaseHandler db = new DataBaseHandler(this);
    public static final String READ_HUMIDADE_TEMPERATURA = "http://" + IP + ":" + PORT + "/sid/getGraphHumTemp.php";
    public static final String READ_Cultura = "http://" + IP + ":" + PORT + "/sid/getCultura.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbClear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void drawGraph(View v) {
        Intent i = new Intent(this, GraphicActivity.class);
        startActivity(i);

    }

    public void showAlertas(View v) {
        Intent i = new Intent(this, AlertasActivity.class);
        startActivity(i);
    }

    public void refreshDB(View v) {
        EditText idCultura = ((EditText) (findViewById(R.id.idCultura)));
        if (idCultura.getText() != null && idCultura.getText().length() > 0) {
            writeToDB(idCultura.getText().toString());
            idCultura.onEditorAction(EditorInfo.IME_ACTION_DONE);
            updateNomeCultura();
            updateNumeroMedicoes();
            updateNumeroAlertas();
        } else {
            ((TextView) (findViewById(R.id.nomeCultura_tv))).setText("");
        }

    }

    public void updateNumeroMedicoes() {

        //To Do

        DataBaseReader dbReader = new DataBaseReader(db);

        Cursor cursor = dbReader.ReadHumidadeTemperatura(null);
        int totalMedicoes = cursor.getCount();
        TextView text = (TextView) findViewById(R.id.numeroMedicoesInt);
        text.setText(Integer.toString(totalMedicoes));

    }

    public void updateNumeroAlertas() {

        //To Do
        DataBaseReader dbReader = new DataBaseReader(db);

        Cursor cursor = dbReader.readAlertas();
        int totalAlertas = cursor.getCount();
        TextView text = (TextView) findViewById(R.id.numeroAlertasInt);
        text.setText(Integer.toString(totalAlertas));

    }

    private void updateNomeCultura() {

        //To do?
        DataBaseReader dbReader = new DataBaseReader(db);

        TextView nomeCultura_tv = (TextView) findViewById(R.id.nomeCultura_tv);
        Cursor cursor = dbReader.readCultura();
        String nomeCultura = null;
        while (cursor.moveToNext()) {
            nomeCultura = cursor.getString(cursor.getColumnIndex("NomeCultura"));
        }

        if (nomeCultura != null) {
            nomeCultura_tv.setText(nomeCultura);
            nomeCultura_tv.setTextColor(Color.BLACK);
        } else {
            nomeCultura_tv.setText("Cultura Invalida!");
            nomeCultura_tv.setTextColor(Color.RED);
        }
        nomeCultura_tv.setVisibility(View.VISIBLE);
    }

//A minha base de dados pode não ser exatamente igual à vossa ou podem concluir que é melhor implementar isto de outra maneira, para mudarem a base de dados no android usem as classes DatabaseConfig(criação) e DatabaseHandler(escrita)

    public void writeToDB(String idCultura) {
        try {
            db.dbClear();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HashMap<String, String> params = new HashMap<>();
            params.put("uid", username);
            params.put("pwd", password);
            params.put("idCult", idCultura);
            ConnectionHandler jParser = new ConnectionHandler();

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_Cultura, params);
            double limInfTemp = 0, limSupTemp = 0, limInfHum = 0, limSupHum = 0;
            if (jsonCultura != null) {
                for (int i = 0; i < jsonCultura.length(); i++) {
                    JSONObject c = jsonCultura.getJSONObject(i);
                    String nomeCultura = c.getString("NOMECULTURA");
                    db.insert_Cultura(Integer.parseInt(idCultura), nomeCultura);

                    limInfTemp = c.getDouble("LIMITEINFERIORTEMPERATURA");
                    limSupTemp = c.getDouble("LIMITESUPERIORTEMPERATURA");
                    limInfHum = c.getDouble("LIMITEINFERIORHUMIDADE");
                    limSupHum = c.getDouble("LIMITESUPERIORHUMIDADE");
                    SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putFloat("LIMINFTEMP", (float) limInfTemp);
                    Ed.putFloat("LIMSUPTEMP", (float) limSupTemp);
                    Ed.putFloat("LIMINFHUM", (float) limInfHum);
                    Ed.putFloat("LIMSUPHUM", (float) limSupHum);
                    Ed.apply();
                }
                JSONArray jsonHumidadeTemperatura = jParser.getJSONFromUrl(READ_HUMIDADE_TEMPERATURA, params);
                if (jsonHumidadeTemperatura != null) {
                    for (int i = 0; i < jsonHumidadeTemperatura.length(); i++) {
                        JSONObject c = jsonHumidadeTemperatura.getJSONObject(i);
                        int idMedicao = c.getInt("IDMEDICAO");
                        String horaMedicao = c.getString("HORAMEDICAO");
                        double valorMedicaoTemperatura = c.getDouble("VALORMEDICAOTEMPERATURA");
                        double valorMedicaoHumidade = c.getDouble("VALORMEDICAOHUMIDADE");
                        String dataMedicao = c.getString("DATAMEDICAO");
                        db.insert_Humidade_Temperatura(idMedicao, horaMedicao, valorMedicaoTemperatura, valorMedicaoHumidade, dataMedicao);
                        validate(limInfTemp, limSupTemp, horaMedicao, valorMedicaoTemperatura, valorMedicaoTemperatura, dataMedicao, "Temperatura");
                        validate(limInfHum, limSupHum, horaMedicao, valorMedicaoTemperatura, valorMedicaoHumidade, dataMedicao, "Humidade");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void validate(double limInfHum, double limSupHum, String horaMedicao, double valorMedicaoTemperatura, double valorMedicaoHumidade, String dataMedicao, String variable) {
        if (valorMedicaoHumidade - limInfHum >= 2) {
            double valorMedicao = valorMedicaoHumidade;
            String nomeVariavel = variable;
            String alerta = "Próx. Lim. Inf.";
            db.insert_Alertas(dataMedicao, valorMedicao, horaMedicao, nomeVariavel, alerta);
        } else if (valorMedicaoHumidade <= limInfHum) {
            double valorMedicao = valorMedicaoTemperatura;
            String nomeVariavel = variable;
            String alerta = "Lim. Inf. Ult.";
            db.insert_Alertas(dataMedicao, valorMedicao, horaMedicao, nomeVariavel, alerta);
        } else {
            if (limSupHum - valorMedicaoHumidade <= 2) {
                double valorMedicao = valorMedicaoHumidade;
                String nomeVariavel = variable;
                String alerta = "Próx. Lim. Sup.";
                db.insert_Alertas(dataMedicao, valorMedicao, horaMedicao, nomeVariavel, alerta);
            } else if (valorMedicaoHumidade >= limSupHum) {
                double valorMedicao = valorMedicaoTemperatura;
                String nomeVariavel = variable;
                String alerta = "Lim. Sup. Ult.";
                db.insert_Alertas(dataMedicao, valorMedicao, horaMedicao, nomeVariavel, alerta);
            }
        }
    }

}
