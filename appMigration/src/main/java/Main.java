import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {

        PahoClient pahoConn = new PahoClient();
        SybaseConnector sybaseConn = new SybaseConnector();
        MongoConnector mongoConn = new MongoConnector();

        pahoConn.setMongoConnection(mongoConn);
        mongoConn.setSybaseConnection(sybaseConn);

        pahoConn.connect();
    }
}
