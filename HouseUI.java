import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HouseUI extends JFrame implements ActionListener {

    JTextField idField, ownerField, locationField, rentField;
    JTextArea output;
    JButton submitBtn, clearBtn;

    public HouseUI() {

        setTitle("House Rental Management System");
        setSize(450,520);
        setLayout(null);
        getContentPane().setBackground(new Color(230,240,255));

        JLabel title = new JLabel("House Rental Form");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(120,10,250,30);

        JLabel idLabel = new JLabel("House ID:");
        JLabel ownerLabel = new JLabel("Owner Name:");
        JLabel locationLabel = new JLabel("Location:");
        JLabel rentLabel = new JLabel("Monthly Rent:");

        idLabel.setBounds(50,70,120,25);
        ownerLabel.setBounds(50,110,120,25);
        locationLabel.setBounds(50,150,120,25);
        rentLabel.setBounds(50,190,120,25);

        idField = new JTextField();
        ownerField = new JTextField();
        locationField = new JTextField();
        rentField = new JTextField();

        idField.setBounds(170,70,180,25);
        ownerField.setBounds(170,110,180,25);
        locationField.setBounds(170,150,180,25);
        rentField.setBounds(170,190,180,25);

        submitBtn = new JButton("Submit");
        clearBtn = new JButton("Clear");

        submitBtn.setBounds(90,240,100,30);
        clearBtn.setBounds(220,240,100,30);

        output = new JTextArea();
        output.setBounds(50,300,320,150);
        output.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(title);
        add(idLabel); add(ownerLabel); add(locationLabel); add(rentLabel);
        add(idField); add(ownerField); add(locationField); add(rentField);
        add(submitBtn); add(clearBtn);
        add(output);

        submitBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==submitBtn) {

            try {

                HouseBean house = new HouseBean();

                house.setHouseId(Integer.parseInt(idField.getText()));
                house.setOwnerName(ownerField.getText());
                house.setLocation(locationField.getText());
                house.setRent(Double.parseDouble(rentField.getText()));

                output.setText(
                        "House Details\n"
                        +"----------------------\n"
                        +"House ID: "+house.getHouseId()
                        +"\nOwner Name: "+house.getOwnerName()
                        +"\nLocation: "+house.getLocation()
                        +"\nMonthly Rent: "+house.getRent()
                        +"\nAnnual Rent: "+house.getAnnualRent()
                );

            }

            catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Please enter valid data");
            }

        }

        if(e.getSource()==clearBtn) {

            idField.setText("");
            ownerField.setText("");
            locationField.setText("");
            rentField.setText("");
            output.setText("");

        }
    }

    public static void main(String[] args) {
        new HouseUI();
    }
}