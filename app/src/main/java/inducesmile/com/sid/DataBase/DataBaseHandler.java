package inducesmile.com.sid.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import inducesmile.com.sid.DataBase.DataBaseConfig;

/**
 * Created by joao on 11/04/2018.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sid.db";

    DataBaseConfig config = new DataBaseConfig();


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(config.SQL_CREATE_HUMIDADE_TEMPERATURA);
        sqLiteDatabase.execSQL(config.SQL_CREATE_ALERTAS);
        sqLiteDatabase.execSQL(config.SQL_CREATE_CULTURA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(config.SQL_DELETE_HUMIDADE_TEMPERATURA);
        sqLiteDatabase.execSQL(config.SQL_DELETE_ALERTAS);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dbClear(){
        getWritableDatabase().execSQL(config.SQL_DELETE_HUMIDADE_TEMPERATURA);
        getWritableDatabase().execSQL(config.SQL_DELETE_ALERTAS);
        getWritableDatabase().execSQL(config.SQL_DELETE_CULTURA);
        onCreate(getWritableDatabase());
    }

    public void insert_Humidade_Temperatura(int idMedicao,int IDCultura,String horaMedicao,double valorMedicaoTemperatura,double valorMedicaoHumidade,String dataMedicao){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_IDMEDICAO,idMedicao);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_HORAMEDICAO,horaMedicao);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOTEMPERATURA,valorMedicaoTemperatura);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_VALORMEDICAOHUMIDADE,valorMedicaoHumidade);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_DATAMEDICAO,dataMedicao);
        values.put(DataBaseConfig.HumidadeTemperatura.COLUMN_NAME_IDCULTURA,IDCultura);

        getWritableDatabase().insert(DataBaseConfig.HumidadeTemperatura.TABLE_NAME,null,values);
    }

    public void insert_Alertas(int idAlerta,String dataMedicao,double valorMedicao,String horaMedicao,String nomeVariavel,String alerta){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_IDALERTA,idAlerta);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_DATAMEDICAO,dataMedicao);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_VALORMEDICAO,valorMedicao);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_HORAMEDICAO,horaMedicao);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_NOMEVARIAVEL,nomeVariavel);
        values.put(DataBaseConfig.Alertas.COLUMN_NAME_ALERTAS,alerta);
        getWritableDatabase().insert(DataBaseConfig.Alertas.TABLE_NAME,null,values);


    }

    public void insert_Cultura(int idCultura,String nomeCultura){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_IDCULTURA,idCultura);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_NOMECULTURA,nomeCultura);
        getWritableDatabase().insert(DataBaseConfig.Cultura.TABLE_NAME,null,values);
    }

}
