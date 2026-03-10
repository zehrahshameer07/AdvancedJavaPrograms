package src;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class Main extends Application {

    // Database Connection
    private Connection conn;

    // --- 10 UI CONTROLS ---
    private TextField tfName = new TextField();
    private TextField tfContact = new TextField();
    private TextField tfRent = new TextField();
    private TextField tfDeposit = new TextField();
    private ComboBox<String> cbType = new ComboBox<>();
    private ComboBox<String> cbLocation = new ComboBox<>();
    private DatePicker dpDate = new DatePicker();
    private CheckBox chkFurnished = new CheckBox("Fully Furnished");
    private RadioButton rbMonthly = new RadioButton("Monthly");
    private RadioButton rbYearly = new RadioButton("Yearly");
    private ToggleGroup paymentGroup = new ToggleGroup();

    // Table
    private TableView<RentalData> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        connectDB(); // Connect on start

        // --- Layout Setup ---
        Label title = new Label(" Smart Rental Management System");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        // Adding Controls to Grid
        grid.add(new Label("Tenant Name:"), 0, 0);   grid.add(tfName, 1, 0);
        grid.add(new Label("Contact No:"), 2, 0);    grid.add(tfContact, 3, 0);
        
        grid.add(new Label("Rent :"), 0, 1);      grid.add(tfRent, 1, 1);
        grid.add(new Label("Deposit :"), 2, 1);   grid.add(tfDeposit, 3, 1);

        grid.add(new Label("Property Type:"), 0, 2);
        cbType.getItems().addAll("Apartment", "Villa", "Studio", "Duplex");
        grid.add(cbType, 1, 2);

        grid.add(new Label("Location:"), 2, 2);
        cbLocation.getItems().addAll("Downtown", "Suburbs", "Uptown", "Industrial");
        grid.add(cbLocation, 3, 2);

        grid.add(new Label("Move-in Date:"), 0, 3);  grid.add(dpDate, 1, 3);
        
        grid.add(new Label("Amenities:"), 2, 3);     grid.add(chkFurnished, 3, 3);

        // Radio Buttons
        rbMonthly.setToggleGroup(paymentGroup);
        rbYearly.setToggleGroup(paymentGroup);
        rbMonthly.setSelected(true);
        HBox radioBox = new HBox(10, rbMonthly, rbYearly);
        grid.add(new Label("Payment Mode:"), 0, 4);  grid.add(radioBox, 1, 4, 3, 1);

        // --- Buttons ---
        Button btnAdd = new Button("ADD");
        Button btnDisplay = new Button("DISPLAY");
        Button btnEdit = new Button("EDIT");
        Button btnDelete = new Button("DELETE");

        // Styling
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnDisplay.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnDelete.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        HBox btnBox = new HBox(15, btnAdd, btnDisplay, btnEdit, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10));

        // --- Table Setup ---
        setupTable();

        // --- Main Container ---
        VBox root = new VBox(15, title, grid, btnBox, table);
        root.setPadding(new Insets(20));
        
        // --- Event Handling ---
        btnAdd.setOnAction(e -> addRecord());
        btnDisplay.setOnAction(e -> displayRecords());
        btnEdit.setOnAction(e -> editRecord());
        btnDelete.setOnAction(e -> deleteRecord());

        // Fill form on row click
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tfName.setText(newVal.getName());
                tfContact.setText(newVal.getContact());
                tfRent.setText(String.valueOf(newVal.getRent()));
                tfDeposit.setText(String.valueOf(newVal.getDeposit()));
                cbType.setValue(newVal.getType());
                cbLocation.setValue(newVal.getLocation());
                if (newVal.getDate() != null && !newVal.getDate().isEmpty()) {
                     dpDate.setValue(LocalDate.parse(newVal.getDate()));
                }
                chkFurnished.setSelected(newVal.isFurnished());
                if ("Monthly".equals(newVal.getPayment())) rbMonthly.setSelected(true);
                else rbYearly.setSelected(true);
            }
        });

        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.setTitle("Lab 3: Smart Rental Application");
        stage.show();
        
        displayRecords(); // Load data on launch
    }

    // ================= DB OPERATIONS =================

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab3_rental_db", "root", "Zehrah@123");
            System.out.println("✅ Database Connected!");
        } catch (Exception e) {
            showAlert("DB Error", "Connection Failed: " + e.getMessage());
        }
    }

    private void addRecord() {
        try {
            String sql = "INSERT INTO rentals (tenant_name, contact_no, property_type, location, rent_amount, security_deposit, move_in_date, is_furnished, payment_mode) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfName.getText());
            ps.setString(2, tfContact.getText());
            ps.setString(3, cbType.getValue());
            ps.setString(4, cbLocation.getValue());
            ps.setDouble(5, Double.parseDouble(tfRent.getText()));
            ps.setDouble(6, Double.parseDouble(tfDeposit.getText()));
            ps.setDate(7, Date.valueOf(dpDate.getValue()));
            ps.setBoolean(8, chkFurnished.isSelected());
            ps.setString(9, rbMonthly.isSelected() ? "Monthly" : "Yearly");
            
            ps.executeUpdate();
            showAlert("Success", "Record Added!");
            displayRecords();
            clearFields();
        } catch (Exception e) { showAlert("Error", "Check inputs: " + e.getMessage()); }
    }

    private void displayRecords() {
        table.getItems().clear();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM rentals");
            while (rs.next()) {
                table.getItems().add(new RentalData(
                    rs.getInt("id"), rs.getString("tenant_name"), rs.getString("contact_no"),
                    rs.getString("property_type"), rs.getString("location"), rs.getDouble("rent_amount"),
                    rs.getDouble("security_deposit"), rs.getDate("move_in_date").toString(),
                    rs.getBoolean("is_furnished"), rs.getString("payment_mode")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void editRecord() {
        RentalData selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            String sql = "UPDATE rentals SET tenant_name=?, contact_no=?, property_type=?, location=?, rent_amount=?, security_deposit=?, move_in_date=?, is_furnished=?, payment_mode=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfName.getText()); ps.setString(2, tfContact.getText());
            ps.setString(3, cbType.getValue()); ps.setString(4, cbLocation.getValue());
            ps.setDouble(5, Double.parseDouble(tfRent.getText())); ps.setDouble(6, Double.parseDouble(tfDeposit.getText()));
            ps.setDate(7, Date.valueOf(dpDate.getValue())); ps.setBoolean(8, chkFurnished.isSelected());
            ps.setString(9, rbMonthly.isSelected() ? "Monthly" : "Yearly");
            ps.setInt(10, selected.getId());
            
            ps.executeUpdate();
            showAlert("Success", "Record Updated!");
            displayRecords();
        } catch (Exception e) { showAlert("Error", "Update Failed: " + e.getMessage()); }
    }

    private void deleteRecord() {
        RentalData selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM rentals WHERE id=?");
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            showAlert("Success", "Record Deleted!");
            displayRecords();
            clearFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================= HELPERS =================

    private void setupTable() {
        TableColumn<RentalData, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> d.getValue().idProperty().asObject());

        TableColumn<RentalData, String> colName = new TableColumn<>("Tenant");
        colName.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<RentalData, String> colRent = new TableColumn<>("Rent");
        colRent.setCellValueFactory(d -> d.getValue().rentProperty().asString());
        
        TableColumn<RentalData, String> colLoc = new TableColumn<>("Location");
        colLoc.setCellValueFactory(d -> d.getValue().locationProperty());

        TableColumn<RentalData, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(d -> d.getValue().typeProperty());

        table.getColumns().addAll(colId, colName, colRent, colLoc, colType);
    }

    private void clearFields() {
        tfName.clear(); tfContact.clear(); tfRent.clear(); tfDeposit.clear();
        cbType.setValue(null); cbLocation.setValue(null); dpDate.setValue(null);
        chkFurnished.setSelected(false);
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    // ================= INNER DATA CLASS =================
    public static class RentalData {
        private final IntegerProperty id;
        private final StringProperty name, contact, type, location, date, payment;
        private final DoubleProperty rent, deposit;
        private final BooleanProperty furnished;

        public RentalData(int id, String name, String contact, String type, String loc, double rent, double dep, String date, boolean furn, String pay) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name); this.contact = new SimpleStringProperty(contact);
            this.type = new SimpleStringProperty(type); this.location = new SimpleStringProperty(loc);
            this.rent = new SimpleDoubleProperty(rent); this.deposit = new SimpleDoubleProperty(dep);
            this.date = new SimpleStringProperty(date); this.furnished = new SimpleBooleanProperty(furn);
            this.payment = new SimpleStringProperty(pay);
        }
        
        // Getters needed for TableView
        public IntegerProperty idProperty() { return id; } public int getId() { return id.get(); }
        public StringProperty nameProperty() { return name; } public String getName() { return name.get(); }
        public StringProperty contactProperty() { return contact; } public String getContact() { return contact.get(); }
        public StringProperty typeProperty() { return type; } public String getType() { return type.get(); }
        public StringProperty locationProperty() { return location; } public String getLocation() { return location.get(); }
        public DoubleProperty rentProperty() { return rent; } public double getRent() { return rent.get(); }
        public DoubleProperty depositProperty() { return deposit; } public double getDeposit() { return deposit.get(); }
        public StringProperty dateProperty() { return date; } public String getDate() { return date.get(); }
        public BooleanProperty furnishedProperty() { return furnished; } public boolean isFurnished() { return furnished.get(); }
        public StringProperty paymentProperty() { return payment; } public String getPayment() { return payment.get(); }
    }
}