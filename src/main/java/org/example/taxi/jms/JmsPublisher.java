package org.example.taxi.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsPublisher {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;

    public JmsPublisher(String brokerUrl, String topicName) throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(topicName);
        producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public void publishText(String text) throws JMSException {
        TextMessage msg = session.createTextMessage(text);
        producer.send(msg);
        System.out.println("[JMS Publisher] sent: " + text);
    }

    public void close() throws JMSException {
        producer.close();
        session.close();
        connection.close();
    }
}
