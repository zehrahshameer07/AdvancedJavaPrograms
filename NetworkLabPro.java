import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class NetworkLabPro extends JFrame {
    private JTextField urlInput;
    private JTextArea display;
    private JButton runBtn, saveBtn;
    private String reportCache = "";

    public NetworkLabPro() {
        // --- UI Setup ---
        setTitle("Advanced Network Analyzer - Lab 5");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel for Controls
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputRow = new JPanel(new BorderLayout(5, 5));
        urlInput = new JTextField("https://www.github.com");
        runBtn = new JButton("Execute Analysis");
        inputRow.add(new JLabel("Target URL: "), BorderLayout.WEST);
        inputRow.add(urlInput, BorderLayout.CENTER);
        inputRow.add(runBtn, BorderLayout.EAST);

        saveBtn = new JButton("Export Report to .txt");
        saveBtn.setEnabled(false);

        controlPanel.add(inputRow);
        controlPanel.add(saveBtn);

        // Display Area (Console Style)
        display = new JTextArea();
        display.setBackground(new Color(20, 20, 20));
        display.setForeground(new Color(0, 255, 65)); // Matrix Green
        display.setFont(new Font("Monospaced", Font.BOLD, 13));
        display.setEditable(false);
        display.setMargin(new Insets(10, 10, 10, 10));

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(display), BorderLayout.CENTER);

        // --- Logic Listeners ---
        runBtn.addActionListener(e -> startNetworkTask());
        saveBtn.addActionListener(e -> exportData());
    }

    private void startNetworkTask() {
        StringBuilder report = new StringBuilder();
        try {
            String target = urlInput.getText();
            URL myUrl = new URL(target);
            
            report.append("=== INETADDRESS ANALYSIS ===\n");
            InetAddress ip = InetAddress.getByName(myUrl.getHost());
            report.append("Host Name      : ").append(ip.getHostName()).append("\n");
            report.append("IP Address     : ").append(ip.getHostAddress()).append("\n");
            report.append("Reachable (5s) : ").append(ip.isReachable(5000)).append("\n\n");

            report.append("=== URLCONNECTION DETAILS ===\n");
            URLConnection conn = myUrl.openConnection();
            report.append("Protocol       : ").append(myUrl.getProtocol()).append("\n");
            report.append("Port           : ").append(myUrl.getPort() == -1 ? myUrl.getDefaultPort() : myUrl.getPort()).append("\n");
            report.append("Content Type   : ").append(conn.getContentType()).append("\n");
            report.append("Content Length : ").append(conn.getContentLength()).append("\n\n");

            report.append("=== HTTP HEADER FIELDS ===\n");
            Map<String, List<String>> headers = conn.getHeaderFields();
            headers.forEach((key, value) -> {
                report.append(String.format("%-20s : %s\n", key, value));
            });

            report.append("\n=== CONTENT PREVIEW (TOP 5 LINES) ===\n");
            // Use try-with-resources to prevent the "Stream Closed" error from your screenshot
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                for (int i = 0; i < 5; i++) {
                    String line = reader.readLine();
                    if (line != null) report.append(line).append("\n");
                }
            }

            reportCache = report.toString();
            display.setText(reportCache);
            saveBtn.setEnabled(true);

        } catch (Exception ex) {
            display.setText("CRITICAL ERROR: " + ex.getMessage());
            saveBtn.setEnabled(false);
        }
    }

    private void exportData() {
        try (PrintWriter writer = new PrintWriter("Network_Lab_Report.txt")) {
            writer.println("Generated on: " + new Date());
            writer.println(reportCache);
            JOptionPane.showMessageDialog(this, "Report saved as 'Network_Lab_Report.txt'");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NetworkLabPro().setVisible(true));
    }
}