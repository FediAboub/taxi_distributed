package org.example.taxi.model;

import java.io.Serializable;

public class RideRequest implements Serializable {
    private String id;
    private String clientName;
    private String pickup;
    private String destination;

    public RideRequest() {}

    public RideRequest(String id, String clientName, String pickup, String destination) {
        this.id = id;
        this.clientName = clientName;
        this.pickup = pickup;
        this.destination = destination;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    @Override
    public String toString() {
        return "RideRequest{id='" + id + "', client='" + clientName + "', pickup='" + pickup + "', dest='" + destination + "'}";
    }
}
