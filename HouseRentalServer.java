import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class HouseRentalServer extends JFrame {
    private JTextArea logArea;
    private JTextField houseDetails;
    private static final String MULTICAST_IP = "230.0.0.1";
    private static final int UDP_PORT = 4446;
    private static final int TCP_PORT = 5000;

    public HouseRentalServer() {
        setTitle("Elite Rentals - Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Custom UI Design
        logArea = new JTextArea();
        logArea.setBackground(new Color(43, 43, 43));
        logArea.setForeground(new Color(255, 204, 0)); // Gold text
        logArea.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        houseDetails = new JTextField();
        JButton broadcastBtn = new JButton("Post New House");
        broadcastBtn.setBackground(new Color(0, 123, 255));
        broadcastBtn.setForeground(Color.WHITE);

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.add(new JLabel(" New Listing: "), BorderLayout.WEST);
        p.add(houseDetails, BorderLayout.CENTER);
        p.add(broadcastBtn, BorderLayout.EAST);

        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        broadcastBtn.addActionListener(e -> postHouse(houseDetails.getText()));
        
        // Start TCP thread to register tenants
        new Thread(this::startRegistry).start();
    }

    private void startRegistry() {
        try (ServerSocket server = new ServerSocket(TCP_PORT)) {
            logArea.append("[SYSTEM] Rental Registry Active...\n");
            while (true) {
                Socket tenant = server.accept();
                logArea.append("[TENANT JOINED] " + tenant.getInetAddress() + " registered for updates.\n");
                tenant.close();
            }
        } catch (IOException e) { logArea.append("Error: " + e.getMessage() + "\n"); }
    }

    private void postHouse(String info) {
        try (DatagramSocket s = new DatagramSocket()) {
            byte[] buf = ("NEW LISTING: " + info).getBytes();
            DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_IP), UDP_PORT);
            s.send(pack);
            logArea.append("[BROADCASTED] " + info + "\n");
            houseDetails.setText("");
        } catch (Exception e) { logArea.append("Broadcast Error: " + e.getMessage() + "\n"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HouseRentalServer().setVisible(true));
    }
}