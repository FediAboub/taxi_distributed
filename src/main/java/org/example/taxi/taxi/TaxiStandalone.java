package org.example.taxi.taxi;

import org.example.taxi.rmi.TaxiRmiImpl;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.util.UUID;

public class TaxiStandalone {

    public static void main(String[] args) throws Exception {
        String broker = "tcp://localhost:61616";
        String topicName = "taxiEvents";
        String taxiId = args.length > 0 ? args[0] : "Taxi-" + UUID.randomUUID().toString().substring(0,6);

        // 1) Start RMI object and bind to registry so server can call taxi via RMI if needed
        TaxiRmiImpl rmiImpl = new TaxiRmiImpl(taxiId);
        try {
            LocateRegistry.getRegistry(1099); // ensure registry exists
            Naming.rebind("//localhost/" + taxiId, rmiImpl);
            System.out.println("[Taxi] Bound RMI as //localhost/" + taxiId);
        } catch (Exception e) {
            System.err.println("[Taxi] Failed to bind RMI: " + e.getMessage());
            // still continue (server may lookup other taxi IDs)
        }

        // 2) Subscribe to JMS Topic
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(broker);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            if (message instanceof TextMessage) {
                try {
                    String text = ((TextMessage) message).getText();
                    System.out.println("[Taxi " + taxiId + "] Received JMS: " + text);
                    if (text.startsWith("NEW_RIDE|")) {
                        String[] parts = text.split("\\|");
                        String rideId = parts[1];
                        System.out.println("[Taxi " + taxiId + "] Trying to accept ride " + rideId);

                        // Accept via RMI call to server? In our simplified flow, taxi calls server RMI or server calls taxi.
                        // Here we'll simulate acceptance by calling local RMI impl method directly (or remote server could call Taxi RMI).
                        boolean accepted = rmiImpl.acceptRide(rideId); // local call
                        if (accepted) {
                            System.out.println("[Taxi " + taxiId + "] Accepted ride " + rideId);
                            // Optionally call server SOAP or REST to notify acceptance (omitted here)
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("[Taxi " + taxiId + "] Listening to JMS topic " + topicName + " ... Press Ctrl+C to stop.");
        // keep alive
        Thread.currentThread().join();
        // cleanup omitted for brevity
    }
}
