package org.example.taxi.soap;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService
public class ManagementSoapService {

    private static List<String> completedRides = new ArrayList<>();

    @WebMethod
    public String ping() {
        return "SOAP Management Service alive";
    }

    @WebMethod
    public void addCompletedRide(String rideId) {
        completedRides.add(rideId);
    }

    @WebMethod
    public String[] getCompletedRides() {
        return completedRides.toArray(new String[0]);
    }
}
