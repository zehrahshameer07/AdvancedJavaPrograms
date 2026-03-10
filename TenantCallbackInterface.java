import java.rmi.*;
public interface TenantCallbackInterface extends Remote {
    void notifyNewHouse(String details) throws RemoteException;
}