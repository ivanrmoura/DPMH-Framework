package br.ufma.lsdi.bpad.component;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Connection;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ConnectionManager is the component that manages the communication with the MQTT Broker.
 * This component initiates the connection with the Broker, subscribes to the services, and posts received messages on the bus.
 *
 * @author Ivan Moura
 *
 */

@Component
public class ConnectionManager {

    private Publisher publisher;

    //private String host ="broker.emqx.io";//200.137.134.98

    @Value("${broker.host}")
    private String host;

    @Value("${event.topic}")
    private String eventTopic;

    @Value("${client.id}")
    private String clientId;

    @Autowired
    private Gson gson;

    private Connection con;


    public void startConnect(){

        con = ConnectionFactory.createConnection();
        con.setHost(host);
        con.setClientId(clientId);
        con.setCleanSession(false);

        con.addConnectionListener(new IConnectionListener() {
            @Override
            public void onConnectionEstablished() {
                startBehaviorEventPersistence();
                System.out.println("conectado ao broker");
            }

            @Override
            public void onConnectionEstablishmentFailed() {

            }

            @Override
            public void onConnectionLost() {

            }

            @Override
            public void onDisconnectedNormally() {

            }
        });

        con.connect();
        CDDL cddl = CDDL.getInstance();
        cddl.setConnection(con);
        cddl.startService();

        publisher = PublisherFactory.createPublisher();
        publisher.addConnection(con);


    }

    public Subscriber createSubscriber(){
        Subscriber subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(con);
        return subscriber;
    }

    public void startBehaviorEventPersistence(){
        Subscriber subscriber = createSubscriber();

        subscriber.subscribeServiceByName(eventTopic);

        subscriber.setSubscriberListener((message) -> {
            if (message != null) {
                System.out.println(gson.toJson(message));  // todo persistir evento
            }

        });
    }

    public void publishMessage(Message message){
        publisher.publish(message);
    }


}
