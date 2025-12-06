package org.example.taxi;

import org.example.taxi.jms.JmsPublisher;
import org.example.taxi.rest.TaxiRestResource;
import org.example.taxi.soap.ManagementSoapService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.server.ResourceConfig;

import javax.jws.Endpoint;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerApp {

    public static final String JMS_BROKER = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "taxiEvents";

    public static void main(String[] args) throws Exception {
        // 1) Start JMS publisher
        JmsPublisher publisher = new JmsPublisher(JMS_BROKER, TOPIC_NAME);
        TaxiRestResource.setPublisher(publisher);
        System.out.println("[Server] JMS Publisher started.");

        // 2) Start SOAP service on http://localhost:9000/soap/management
        ManagementSoapService soapService = new ManagementSoapService();
        Endpoint.publish("http://localhost:9000/soap/management", soapService);
        System.out.println("[Server] SOAP service published at http://localhost:9000/soap/management?wsdl");

        // 3) Start RMI registry (for server to be able to lookup taxis if needed)
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("[Server] RMI registry created on port 1099.");
        } catch (Exception e) {
            System.out.println("[Server] RMI registry already present.");
        }

        // 4) Start REST server (Jetty + Jersey)
        ResourceConfig config = new ResourceConfig();
        config.register(TaxiRestResource.class);
        config.register(org.glassfish.jersey.jsonb.JsonBindingFeature.class);
        ServletContainer servletContainer = new ServletContainer(config);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new org.eclipse.jetty.servlet.ServletHolder(servletContainer), "/*");

        Server server = new Server(8080);
        server.setHandler(context);

        try {
            server.start();
            System.out.println("[Server] REST service started at http://localhost:8080/api/taxi/request (POST)");
            System.out.println("Ready. Use Ctrl+C to stop.");
            server.join();
        } finally {
            publisher.close();
            server.stop();
        }
    }
}
