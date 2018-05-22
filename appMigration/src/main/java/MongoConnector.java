import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


public class MongoConnector {
    private final int MIGRATION_INTERVAL = 30000;
    private MongoClient mongo;
    private MongoDatabase mongoDatabase;
    private String databaseName;
    private String collectionName;
    private SybaseConnector sybaseConnector;
    private Queue<JSONObject> queueDataToTransmit = new LinkedList<JSONObject>();

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
                    System.out.println(" MongoDB Migration Starting! " + size + " items on queue.");
                    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                    while (!queueDataToTransmit.isEmpty()) {
                        JSONObject obj = queueDataToTransmit.poll();
                        Document doc = Document.parse(obj.toString());
                        collection.insertOne(doc);
                    }
                    FindIterable<Document> docs = mongoDatabase.getCollection(collectionName).find();
                    for (Document doc : docs) {
                        try {
                            JSONObject jsonObj = new JSONObject(doc.toJson());
                            String dateRec = (String) jsonObj.get("date");
                            String day, month, year, altDate;
                            year = dateRec.split("/")[0];
                            month = dateRec.split("/")[1];
                            day = dateRec.split("/")[2];
                            altDate = year + "/" + month + "/" + day;
                            if (Integer.parseInt(day) > Integer.parseInt(year)) {
                                altDate = day + "/" + month + "/" + year;
                                jsonObj.put("date", altDate);
                            }
                            sybaseConnector.addToQueue(jsonObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mongoDatabase.getCollection(collectionName).deleteMany(new Document());
                    System.out.println("All collection objects deleted.");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(dtf.format(now) + " -- MongoDB Migration completed. " + size + " lines inserted.");
                    if (mongo != null) {
                        mongo.close();
                        System.out.println("MongoDB connection closed.");
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

    public MongoConnector() {
        databaseName = "SID";
        collectionName = "HUMIDADETEMPERATURA";
        new MigrationThread().start();
    }


    public boolean connect() {
        String mongoClientURI = "mongodb://" + "migrator" + ":" + "migrator" + "@" + "localhost" + ":" + 27017 + "/?authSource=SID";
        mongo = new MongoClient(new MongoClientURI(mongoClientURI));
        mongoDatabase = mongo.getDatabase(databaseName);
        System.out.println("Mongo connection successful!");
        return true;
    }

    public void addToQueue(JSONObject jsonObj) {
        if (jsonObj != null) {
            queueDataToTransmit.add(jsonObj);
            System.out.println("MONGO queue added: " + jsonObj.toString());
        }
    }


    public void setSybaseConnection(SybaseConnector sybaseConnection) {
        this.sybaseConnector = sybaseConnection;
    }
}