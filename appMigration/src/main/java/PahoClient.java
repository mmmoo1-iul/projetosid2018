import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class PahoClient implements MqttCallback {

    private MqttClient client;
    private static final String topic = "sid_lab_2018_g27";
    private MongoConnector mongoConnection;

    public PahoClient() {

    }

    public static void main(String[] args) {
        new PahoClient();
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println(">>>>Connection lost, reconnecting...");
        connect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        MongoConnector mongoConnector = (MongoConnector) mongoConnection;
        String msg = message.toString().trim();
        if (msg.startsWith("{") && msg.endsWith("}"))
            mongoConnector.addToQueue(new JSONObject(msg));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void connect() {
        try {
            client = new MqttClient("tcp://iot.eclipse.org:1883", "");
            client.connect();
            client.setCallback(this);
            client.subscribe(topic);
            System.out.println("Paho connection!");
        } catch (MqttException e) {
            System.out.println("[xxx Paho connection error xxx]");
            try {
                Thread.sleep(10000);
                connect();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }


    public void setMongoConnection(MongoConnector mongoConnection) {
        this.mongoConnection = mongoConnection;
    }
}