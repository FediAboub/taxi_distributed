package org.example.taxi.rest;

import org.example.taxi.model.RideRequest;
import org.example.taxi.jms.JmsPublisher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/taxi")
public class TaxiRestResource {

    private static JmsPublisher publisher;

    // Publisher is set by ServerApp at startup
    public static void setPublisher(JmsPublisher p) {
        publisher = p;
    }

    @POST
    @Path("/request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTaxi(RideRequest req) {
        if (req.getId() == null || req.getId().isEmpty()) {
            req.setId(UUID.randomUUID().toString());
        }
        try {
            String payload = "NEW_RIDE|" + req.getId() + "|" + req.getClientName() + "|" + req.getPickup() + "|" + req.getDestination();
            publisher.publishText(payload);
            return Response.ok(req).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Failed to publish ride").build();
        }
    }
}
