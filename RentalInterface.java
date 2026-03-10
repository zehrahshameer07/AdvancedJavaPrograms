import java.rmi.*;
public interface RentalInterface extends Remote {
    void registerTenant(TenantCallbackInterface tenant) throws RemoteException;
    void postNewHouse(String details) throws RemoteException;
}