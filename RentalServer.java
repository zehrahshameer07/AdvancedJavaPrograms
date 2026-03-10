import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class RentalServer extends UnicastRemoteObject implements RentalInterface {
    private ArrayList<TenantCallbackInterface> tenants = new ArrayList<>();
    private JTextArea logArea;

    public RentalServer(JTextArea log) throws RemoteException { this.logArea = log; }

    public synchronized void registerTenant(TenantCallbackInterface tenant) throws RemoteException {
        tenants.add(tenant);
        logArea.append("🏠 [NEW TENANT] Subscribed for live alerts.\n");
    }

    public synchronized void postNewHouse(String details) throws RemoteException {
        logArea.append("📢 [BROADCAST] " + details + "\n");
        for (TenantCallbackInterface t : tenants) {
            try { t.notifyNewHouse(details); } catch (Exception e) { /* Handle stale client */ }
        }
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Elite Rental Agency - Admin");
            JTextArea area = new JTextArea();
            area.setBackground(new Color(20, 20, 20)); area.setForeground(Color.GREEN);
            area.setFont(new Font("Monospaced", Font.BOLD, 13));
            
            JTextField input = new JTextField();
            JButton btn = new JButton("Post Listing");
            btn.setBackground(new Color(0, 102, 204)); btn.setForeground(Color.WHITE);

            // 1. Start Registry Internally
            Registry reg;
            try { reg = LocateRegistry.createRegistry(1099); } 
            catch (Exception e) { reg = LocateRegistry.getRegistry(1099); }
            
            // 2. Bind the Service
            RentalServer server = new RentalServer(area);
            reg.rebind("RentalService", server);

            btn.addActionListener(e -> {
                try { server.postNewHouse(input.getText()); input.setText(""); } catch (Exception ex) {}
            });

            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(area), "Center");
            JPanel p = new JPanel(new BorderLayout());
            p.add(input, "Center"); p.add(btn, "East");
            frame.add(p, "South");
            frame.setSize(500, 400); frame.setVisible(true);
            area.append("[SYSTEM] Server Ready on Port 1099...\n");
        } catch (Exception e) { e.printStackTrace(); }
    }
}