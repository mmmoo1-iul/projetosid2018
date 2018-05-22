// Example Java Program - Sybase SQL Anywhere 12 Database Connectivity with JDBC 4.0

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class SybaseConnector {
    private static final String driver = "jdbc4";
    private Connection con;
    private ResultSet rs;
    private Statement stmt;
    private static String SQL_INSERT = "INSERT INTO DBA.HUMIDADETEMPERATURA(VALORMEDICAOTEMPERATURA,VALORMEDICAOHUMIDADE,DATAMEDICAO,HORAMEDICAO) VALUES(?, ?, ?, ?)";
    private Queue<JSONObject> queueDataToTransmit = new LinkedList<JSONObject>();
    private final int MIGRATION_INTERVAL = 30000;
    private double tempSum, humSum, tempAvg, humAvg = 0;
    private long globalCount = 0;

    public class MigrationThread extends Thread {

        public MigrationThread() {

        }

        @Override
        public void run() {
            startMigrationCycle();
        }

        private void startMigrationCycle() {
            int size = 0;
            while (true) {
                if (queueDataToTransmit.size() > 0 && connect()) {
                    size = queueDataToTransmit.size();
                    System.out.println("Migration Starting! " + size + " items on queue.");
                    PreparedStatement statement = null;
                    while (!queueDataToTransmit.isEmpty()) {
                        JSONObject obj = queueDataToTransmit.poll();
                        try {
                            double objTemp, objHum;
                            objTemp = Double.parseDouble((String) obj.get("temperature"));
                            objHum = Double.parseDouble((String) obj.get("humidity"));
                            tempSum += objTemp;
                            humSum += objHum;
                            globalCount++;
                            tempAvg = tempSum / globalCount;
                            humAvg = humSum / globalCount;
                            if (objTemp >= tempAvg * 0.85
                                    && objTemp <= tempAvg * 1.15
                                    && objHum <= humAvg * 1.15
                                    && objHum >= humAvg * 0.85)
                                try {
                                    statement = getPreparedStatementForSQLExecute(statement, 0, obj);
                                    statement.executeUpdate();
                                } catch (SQLException e) {
                                    connect();
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (statement != null) {
                        try {
                            statement.close();
                            con.close();
                            System.out.println("Sybase connection closed.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Temperature average : " + tempAvg + " || Humidity average : " + humAvg);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(dtf.format(now) + " -- Migration completed. " + size + " lines inserted.");
                }
                try {
                    sleep(MIGRATION_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SybaseConnector() {
        new MigrationThread().start();
    }

    public boolean connect() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:sqlanywhere:uid=Sensor;pwd=sensor");
            System.out.println("Sybase Connection -- Success!");
            return true;
        } catch (SQLException e) {
            System.out.println("[xxx Sybase connection error xxx]");
            return false;
        }
    }

    public void addToQueue(JSONObject jsObject) {
        queueDataToTransmit.add(jsObject);
        System.out.println("Added " + jsObject.toString() + " to queue.");
        System.out.println("Queue size : " + queueDataToTransmit.size());
    }

    private PreparedStatement getPreparedStatementForSQLExecute(PreparedStatement statement, int executeStatus, JSONObject jsonObj) {
        try {
            statement = con.prepareStatement(SQL_INSERT);
        } catch (SQLException e) {
            System.err.println("ERRO: con.prepareStatement");
        }
        System.out.println("[------]A ENVIAR DA QUEUE: " + jsonObj.toString());
        setPreparedStatementValues(jsonObj, statement, executeStatus);
        return statement;
    }

    private JSONObject getJsonObjectFromString(String lastSensorDataToTransmit) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(lastSensorDataToTransmit);
        } catch (JSONException e) {
            System.err.println("ERRO: ao fazer parse da string com dados do sensor.");
        }
        return jsonObj;
    }

    private void setPreparedStatementValues(JSONObject jsObject, PreparedStatement statement, int executeStatus) {

        try {
            try {
                statement.setString(1, (String) jsObject.get("temperature"));
            } catch (JSONException e) {
                System.out.println("Erro: VALORMEDICAOHUMIDADE");
            }
            try {
                statement.setString(2, (String) jsObject.get("humidity"));
            } catch (JSONException e) {
                System.out.println("Erro: VALORMEDICAOTEMPERATURA");
            }
            try {
                statement.setString(3, (String) jsObject.get("date"));
            } catch (JSONException e) {
                System.out.println("Erro: DATAMEDICAO");
            }
            try {
                statement.setString(4, (String) jsObject.get("time"));
            } catch (JSONException e) {
                System.out.println("Erro: HORAMEDICAO");
            }
        } catch (SQLException e) {
            System.out.println("Erro: preparedStatement");
        }
    }

}
