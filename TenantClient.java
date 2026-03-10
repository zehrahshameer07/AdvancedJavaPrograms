import javax.swing.*;
import java.awt.*;
import java.net.*;

public class TenantClient extends JFrame {
    private JTextArea display;
    private JButton joinBtn;
    private MulticastSocket mSocket;
    private InetAddress group;

    public TenantClient() {
        setTitle("Tenant - Listing Alerts");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        display = new JTextArea("Click below to start receiving rental alerts...\n");
        display.setFont(new Font("SansSerif", Font.PLAIN, 13));
        joinBtn = new JButton("Subscribe to House Alerts");
        
        add(joinBtn, BorderLayout.NORTH);
        add(new JScrollPane(display), BorderLayout.CENTER);

        joinBtn.addActionListener(e -> connectToAgency());
    }

    private void connectToAgency() {
        try {
            // Requirement: TCP Registration
            Socket tcp = new Socket("localhost", 5000);
            tcp.close();
            display.append("[TCP] Registered with Elite Rentals.\n");

            // Requirement: UDP Multicast
            mSocket = new MulticastSocket(4446);
            group = InetAddress.getByName("230.0.0.1");
            mSocket.joinGroup(group);
            
            joinBtn.setEnabled(false);
            new Thread(this::listenForHouses).start();
        } catch (Exception e) { display.append("Connection Failed: " + e.getMessage() + "\n"); }
    }

    private void listenForHouses() {
        try {
            while (true) {
                byte[] b = new byte[256];
                DatagramPacket p = new DatagramPacket(b, b.length);
                mSocket.receive(p);
                display.append("[ALERT] " + new String(p.getData()).trim() + "\n");
            }
        } catch (Exception e) { display.append("Disconnected.\n"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TenantClient().setVisible(true));
    }
}