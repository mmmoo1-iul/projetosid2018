package inducesmile.com.sid.App;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;

public class AlertasActivity extends AppCompatActivity {

    DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas);
        Cursor alertasCursor= getAlertasCursor();
        Cursor culturaCursor = getCulturaCursor();
        updateNomeCultura(culturaCursor);
        listAlertas(alertasCursor);
    }

    public Cursor getCulturaCursor(){
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readCultura();
        return cursor;
    }

    public Cursor getAlertasCursor(){
        //To do
        DataBaseReader dbReader = new DataBaseReader(db);
        Cursor cursor = dbReader.readAlertas();
        return cursor;
    }
    private void updateNomeCultura(Cursor culturaCursor){
        String nome=null;
        while (culturaCursor.moveToNext()){
            nome = culturaCursor.getString(culturaCursor.getColumnIndex("NomeCultura"));
        }

        TextView tv = findViewById(R.id.nome_cultura_alerta_tv);
        if (nome!=null){
        tv.setText(nome);}
    }

    private void listAlertas(Cursor alertasCursor){

        TableLayout table = findViewById(R.id.tableAlertas);
        while (alertasCursor.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView nomeVariavel = new TextView(this);
            nomeVariavel.setText(alertasCursor.getString(alertasCursor.getColumnIndex("NomeVariavel")));
            nomeVariavel.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView data = new TextView(this);
            data.setText(alertasCursor.getString(alertasCursor.getColumnIndex("DataMedicao")));
            data.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView hora = new TextView(this);
            String fullHora = alertasCursor.getString(alertasCursor.getColumnIndex("HoraMedicao"));
            String[] splitter = fullHora.split(":");
            String horaFormatted = splitter[0]+":"+splitter[1];
            hora.setText(horaFormatted);
            hora.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);


            TextView valor = new TextView(this);
            valor.setText(Double.toString(alertasCursor.getDouble(alertasCursor.getColumnIndex("ValorMedicao"))));
            valor.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView alerta = new TextView(this);
            alerta.setText(alertasCursor.getString(alertasCursor.getColumnIndex("Alertas")));
            alerta.setPadding(dpAsPixels(16),dpAsPixels(5),20,0);

            row.addView(nomeVariavel);
            row.addView(data);
            row.addView(hora);
            row.addView(valor);
            row.addView(alerta);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }


    }

private int dpAsPixels(int dp){
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp*scale + 0.5f);

}


}
