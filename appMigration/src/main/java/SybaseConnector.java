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
    private Queue<JSONObject> queueDataToTransmit = new LinkedList<JSONObject>(), failureList = new LinkedList<JSONObject>();
    private final int MIGRATION_INTERVAL = 30000;
    private double lastTemp = 0, lastHum = 0;

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
                    PreparedStatement statement = null;
                    while (!queueDataToTransmit.isEmpty()) {
                        JSONObject obj = queueDataToTransmit.poll();
                        System.out.println("Migration Starting! " + queueDataToTransmit.size() + " item(s) on queue.");
                        try {
                            double objTemp, objHum;
                            if (obj.toString().indexOf("nan") == -1) {
                                objTemp = Double.parseDouble((String) obj.get("temperature"));
                                objHum = Double.parseDouble((String) obj.get("humidity"));
                            } else {
                                continue;
                            }

                            if (lastHum == 0 && lastTemp == 0) {
                                try {
                                    statement = getPreparedStatementForSQLExecute(statement, 0, obj);
                                    statement.executeUpdate();
                                    lastHum = objHum;
                                    lastTemp = objTemp;
                                } catch (SQLException e) {
                                }
                            } else {
                                if (objHum <= lastHum + 4 && objHum >= lastHum - 4
                                        && objTemp <= lastTemp + 2 && objTemp >= lastTemp - 2) {
                                    try {
                                        statement = getPreparedStatementForSQLExecute(statement, 0, obj);
                                        statement.executeUpdate();
                                        lastHum = objHum;
                                        lastTemp = objTemp;
                                        failureList.clear();
                                    } catch (SQLException e) {
                                    }
                                } else {
                                    failureList.add(obj);
                                    if (failureList.size() == 12) {
                                        while (!failureList.isEmpty()) {
                                            System.out.println("Migration Starting! " + failureList.size() + " item(s) on queue.");
                                            JSONObject objFailure = failureList.poll();
                                            double objTempFailure, objHumFailure;
                                            objTempFailure = Double.parseDouble((String) objFailure.get("temperature"));
                                            objHumFailure = Double.parseDouble((String) objFailure.get("humidity"));
                                            if (failureList.size() == 11) {
                                                try {
                                                    statement = getPreparedStatementForSQLExecute(statement, 0, obj);
                                                    statement.executeUpdate();
                                                    lastHum = objHumFailure;
                                                    lastTemp = objTempFailure;
                                                } catch (SQLException e) {
                                                }
                                            } else {
                                                if (objHumFailure <= lastHum + 4 && objHumFailure >= lastHum - 4
                                                        && objTempFailure <= lastTemp + 2 && objTempFailure >= lastTemp - 2) {
                                                    try {
                                                        statement = getPreparedStatementForSQLExecute(statement, 0, obj);
                                                        statement.executeUpdate();
                                                        lastHum = objHumFailure;
                                                        lastTemp = objTempFailure;
                                                    } catch (SQLException e) {
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Sybase Migration Over.");
                    if (statement != null) {
                        try {
                            statement.close();
                            con.close();
                            System.out.println("Sybase connection closed.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
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

    private PreparedStatement getPreparedStatementForSQLExecute(PreparedStatement statement,
                                                                int executeStatus, JSONObject jsonObj) {
        try {
            statement = con.prepareStatement(SQL_INSERT);
        } catch (SQLException e) {
            System.err.println("ERRO: con.prepareStatement");
        }
//        System.out.println("[------]A ENVIAR DA QUEUE: " + jsonObj.toString());
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
