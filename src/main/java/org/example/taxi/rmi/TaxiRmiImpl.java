package org.example.taxi.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class TaxiRmiImpl extends UnicastRemoteObject implements TaxiRmi {
    private final String taxiId;

    public TaxiRmiImpl(String taxiId) throws RemoteException {
        super();
        this.taxiId = taxiId;
    }

    @Override
    public boolean acceptRide(String rideId) throws RemoteException {
        System.out.println("[RMI] Taxi " + taxiId + " accepted ride " + rideId);
        // simulate acceptance logic (return true)
        return true;
    }

    @Override
    public void ping() throws RemoteException {
        System.out.println("[RMI] ping from " + taxiId);
    }
}
