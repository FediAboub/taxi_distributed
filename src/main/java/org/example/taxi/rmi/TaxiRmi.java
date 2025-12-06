package org.example.taxi.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaxiRmi extends Remote {
    boolean acceptRide(String rideId) throws RemoteException;
    void ping() throws RemoteException;
}
