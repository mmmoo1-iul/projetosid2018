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

import java.util.Calendar;
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
    public static final String READ_HUMIDADE_TEMPERATURA = "http://" + IP + ":" + PORT + "/sid/getHumidadeTemperatura.php";
    public static final String READ_Cultura = "http://" + IP + ":" + PORT + "/sid/getCultura.php";
    private String yearString, monthString, dayString, sybaseToday, today;
    private MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("date")) {
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            yearString = "" + yearMonthDay[0];
            monthString = "" + yearMonthDay[1];
            dayString = "" + yearMonthDay[2];
        } else {
            yearString = "" + Calendar.getInstance().get(Calendar.YEAR);
            monthString = "" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
            dayString = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        dateToString();
        sybaseToday = yearString + "-" + monthString + "-" + dayString;
        today = dayString + "/" + monthString + "/" + yearString;
        transformDateString();

    }

    public void showDatePicker(View v) {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Intent intent = new Intent(instance, DatePickerActivity.class);
                startActivity(intent);
                return null;
            }
        }.execute();
    }

    private void dateToString() {
        if (Integer.parseInt(monthString) < 10) {
            monthString = "0" + monthString;
        }
        if (Integer.parseInt(dayString) < 10) {
            dayString = "0" + dayString;
        }
    }

    private void transformDateString() {
        TextView text = (TextView) findViewById(R.id.graphicDate);
        text.setText(today);
        System.out.println(today);
    }

    @Override
    public void onBackPressed() {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Intent i = new Intent(instance, LoginActivity.class);
                startActivity(i);
                //finish();
                return null;
            }
        }.execute();
    }

    public void drawGraph(View v) {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Intent i = new Intent(instance, GraphicActivity.class);
                i.putExtra("date", sybaseToday);
                startActivity(i);
                return null;
            }
        }.execute();
    }

    public void showAlertas(View v) {
        AsyncTask t = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Intent i = new Intent(instance, AlertasActivity.class);
                startActivity(i);
                return null;
            }
        }.execute();

    }

    public void refreshDB(View v) {
        db.dbClear();
        EditText idCultura = ((EditText) (findViewById(R.id.idCultura)));
        if (idCultura.getText() != null && idCultura.getText().length() > 0) {
            SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor Ed = sp.edit();
            Ed.putString("idCult", idCultura.getText().toString());
            Ed.apply();
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
            params.put("date", sybaseToday);
            ConnectionHandler jParser = new ConnectionHandler();

            JSONArray jsonCultura = jParser.getJSONFromUrl(READ_Cultura, params);
            double limInfTemp = 0, limSupTemp = 0, limInfHum = 0, limSupHum = 0;
            if (jsonCultura != null) {

                JSONObject obj = jsonCultura.getJSONObject(0);
                String nomeCultura = obj.getString("NOMECULTURA");
                db.insert_Cultura(Integer.parseInt(idCultura), nomeCultura);

                limInfTemp = obj.getDouble("LIMITEINFERIORTEMPERATURA");
                limSupTemp = obj.getDouble("LIMITESUPERIORTEMPERATURA");
                limInfHum = obj.getDouble("LIMITEINFERIORHUMIDADE");
                limSupHum = obj.getDouble("LIMITESUPERIORHUMIDADE");

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
                        validate(limInfTemp, limSupTemp, horaMedicao, valorMedicaoTemperatura, dataMedicao, "Temperatura");
                        validate(limInfHum, limSupHum, horaMedicao, valorMedicaoHumidade, dataMedicao, "Humidade");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void validate(double limInf, double limSup, String horaMedicao, double valorMedicao, String dataMedicao, String variable) {
        String alerta = "";
        if (valorMedicao - limInf <= 1) {
            alerta = "Próx. Lim. Inf.";
        } else {
            if (limSup - valorMedicao <= 1) {
                alerta = "Próx. Lim. Sup.";
            }
        }
        if (valorMedicao <= limInf) {
            alerta = "Lim. Inf. Ult.";
        }
        if (valorMedicao >= limSup) {
            alerta = "Lim. Sup. Ult.";
        }
        if (alerta != "") {
            db.insert_Alertas(dataMedicao, valorMedicao, horaMedicao, variable, alerta);
        }
    }

}
