package inducesmile.com.sid.DataBase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import inducesmile.com.sid.DataBase.DataBaseConfig;
import inducesmile.com.sid.DataBase.DataBaseHandler;

/**
 * Created by joao on 11/04/2018.
 */

public class DataBaseReader {

    SQLiteDatabase db;

    public DataBaseReader(DataBaseHandler dbHandler){
        db = dbHandler.getReadableDatabase();

    }

    // https://stackoverflow.com/questions/10600670/sqlitedatabase-query-method


    public Cursor ReadHumidadeTemperatura(String data){

        //To Do
       if (data!=null){
           Log.d("dataString",data);
       }

        Cursor cursor = db.query(
                DataBaseConfig.HumidadeTemperatura.TABLE_NAME,   // Nome da tabela
                null,
                        data,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readAlertas(){

        //To do
        Cursor cursor = db.query(
                DataBaseConfig.Alertas.TABLE_NAME,   // Nome da tabela
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readCultura(){
        //To do
        Cursor cursor = db.query(
                DataBaseConfig.Cultura.TABLE_NAME,   // Nome da tabela
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }



}
