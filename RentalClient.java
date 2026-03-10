import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import javax.swing.*;
import java.awt.*;

public class RentalClient extends UnicastRemoteObject implements TenantCallbackInterface {
    private JTextArea area;
    public RentalClient(JTextArea a) throws RemoteException { this.area = a; }

    public void notifyNewHouse(String details) throws RemoteException {
        area.append("✨ CALLBACK: " + details + "\n");
    }

    public static void main(String[] args) {
        try {
            JFrame f = new JFrame("Tenant App");
            JTextArea a = new JTextArea("Connecting...\n");
            a.setFont(new Font("Arial", Font.PLAIN, 14));
            f.add(new JScrollPane(a)); f.setSize(400, 350); f.setVisible(true);

            RentalClient client = new RentalClient(a);
            // Connect to the specific registry name
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            RentalInterface service = (RentalInterface) reg.lookup("RentalService");
            service.registerTenant(client); //
            
            a.append("✅ Connected! Live callbacks active.\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection Failed! Ensure Server is running.");
        }
    }
}