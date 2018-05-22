package inducesmile.com.sid.App;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

import inducesmile.com.sid.DataBase.DataBaseHandler;
import inducesmile.com.sid.DataBase.DataBaseReader;
import inducesmile.com.sid.R;

public class GraphicActivity extends AppCompatActivity {

DataBaseHandler db = new DataBaseHandler(this);
GraphView graph;
DataBaseReader reader;
int year;
int month;
int day;
String yearString;
String monthString;
String dayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        graph = findViewById(R.id.graph);

        if (getIntent().hasExtra("date")){
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            year = yearMonthDay[0];
            month= yearMonthDay[1];
            day=yearMonthDay[2];
        }
        else{
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH)+1;
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        dateToString();
        transformDateString();
        Cursor cursor = getCursor();
        drawGraph(cursor);
    }


    private void dateToString(){
        yearString = Integer.toString(year);
        if (month<10){
            monthString="0"+Integer.toString(month);
        }else{
            monthString=Integer.toString(month);
        }
        if(day<10){
            dayString="0"+Integer.toString(day);
        }
        else{
            dayString=Integer.toString(day);
        }

    }
    private void transformDateString(){
        TextView text = findViewById(R.id.graphicDate);
        text.setText(yearString +"-"+monthString+"-"+dayString);
    }

    public Cursor getCursor(){

        //To do, ir à base de dados buscar o cursor do dia selecionado.

        reader = new DataBaseReader(db);
        Cursor cursor = reader.ReadHumidadeTemperatura("DataMedicao='"+yearString+"-"+monthString+"-"+dayString+"'");
        return cursor;
    }


//A parte do dia selecionado no calendario ser guardado nas variáveis necessárias nesta classe já está feito, não precisam de mexer em nada referente ao calendário a não ser que queiram melhorar o que eu fiz.
    public void showDatePicker(View v){
        Intent intent = new Intent(GraphicActivity.this,DatePickerActivitiy.class);
        startActivity(intent);
        finish();
    }

//Para o gráfico ser desenhado precisam de pelo menos dois valores num dia (é um grafico de linhas), ou seja o cursor entregue a esta função tem de ter registos em duas alturas diferentes no mesmo dia, se quiserem desenhar so com um valor têm de alterar o grafico para um grafico de pontos, este é o link da api que eu usei http://www.android-graphview.org//
    private void drawGraph(Cursor cursor){
        int helper = 0;
        double first_value=0.0;
        double last_value=0.0;

        DataPoint[] datapointsTemperatura = new DataPoint[cursor.getCount()];
        DataPoint[] datapointsHumidade = new DataPoint[cursor.getCount()];

        //Ir a cada entrada, converter os minutos para decimais e por no grafico
        while(cursor.moveToNext()) {
            Integer dataTemperatura = cursor.getInt(cursor.getColumnIndex("ValorMedicaoTemperatura"));
            Integer dataHumidade = cursor.getInt(cursor.getColumnIndex("ValorMedicaoHumidade"));

            String horaString = cursor.getString(cursor.getColumnIndex("HoraMedicao"));
            double horaForGraph = convertHourStringToDouble(horaString);

            if (helper==0)
                first_value=horaForGraph;
            last_value=horaForGraph;

            datapointsTemperatura[helper] = new DataPoint(horaForGraph,dataTemperatura);
            datapointsHumidade[helper] = new DataPoint(horaForGraph,dataHumidade);
            helper++;
        }
        cursor.close();

        //Converter o eixo do X para hora:minutos
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    String horaForGraph = convertDoubleToHourString(value);
                    return horaForGraph;
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        LineGraphSeries<DataPoint> seriesTemperatura = new LineGraphSeries<>(datapointsTemperatura);
        LineGraphSeries<DataPoint> seriesHumidade = new LineGraphSeries<>(datapointsHumidade);

        seriesTemperatura.setColor(Color.RED);
        seriesTemperatura.setTitle("Temperatura");
        seriesHumidade.setTitle("Humidade");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setBackgroundColor(Color.alpha(0));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(first_value);
        graph.getViewport().setMaxX(last_value);


        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(seriesTemperatura);
        graph.addSeries(seriesHumidade);
    }



    private double convertHourStringToDouble(String horaString){
        String[] horaMinutos = horaString.split(":");
        int hora = Integer.parseInt(horaMinutos[0]);
        double minutos = Integer.parseInt(horaMinutos[1]);

        minutos = minutos/0.6;

        String tempstring = Double.toString(minutos);
        String minutosConverted= tempstring.replace(".","");

        horaString = hora+"."+minutosConverted;
        double horaForGraph = Double.parseDouble(horaString);
        return horaForGraph;
    }

    private String convertDoubleToHourString(double value){
        String horaString = Double.toString(value);
        Log.d("horaString",horaString);
        String[] horaMinutos = horaString.split("\\.");
        int hora = Integer.parseInt(horaMinutos[0]);
        String minutosTemp = horaMinutos[1];
        if (minutosTemp.charAt(0)=='0'){
            minutosTemp = minutosTemp.charAt(0) + "." + minutosTemp.substring(1);
        }
        double minutos = Double.parseDouble(minutosTemp.substring(0,Math.min(minutosTemp.length(),7)));

        String tempString;
        //Caso exceção para numeros infinitesimais
        if (minutos > 100){
            String tempString2 = Double.toString(minutos);
            tempString2 = tempString2.replace(".","");
            tempString2=tempString2.substring(0,2)+"."+tempString2.substring(2,tempString2.length());

            minutos = Double.parseDouble(tempString2);
            minutos=minutos+0.1;
        }

        //caso exceção entre 0.05 e 0.1
        if (minutos>10 && minutos<16.6665){

            minutos = minutos * 0.6;
            tempString = Double.toString(minutos);
            tempString= "0"+tempString.charAt(0);
        }
        else{
            minutos = minutos * 0.6;
            tempString = Double.toString(minutos);}

        String minutosConverted = tempString.replace(".","");


        String minutosFormatted = minutosConverted.substring(0,Math.min(minutosConverted.length(),2));
        String horaForGraph = hora+":"+minutosFormatted;
        return horaForGraph;
    }

}
