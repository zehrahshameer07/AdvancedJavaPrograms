import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.sql.*;

public class HomeRentalApp extends Application {

    Connection con;
    Statement stmt;
    ResultSet rs;

    TextField txtId = new TextField();
    TextField txtAddress = new TextField();
    TextField txtRent = new TextField();
    TextField txtOwner = new TextField();
    TextField txtContact = new TextField();

    ImageView imageView = new ImageView();
    TextArea metaArea = new TextArea();

    TableView<HouseRecord> table = new TableView<>();

    @Override
    public void start(Stage stage) {

        // ===== FORM =====
        GridPane form = new GridPane();
        form.setPadding(new Insets(15));
        form.setVgap(10);
        form.setHgap(10);

        txtId.setEditable(false);
        txtOwner.setEditable(false);
        txtContact.setEditable(false);

        form.add(new Label("House ID"), 0, 0);
        form.add(txtId, 1, 0);

        form.add(new Label("Address"), 0, 1);
        form.add(txtAddress, 1, 1);

        form.add(new Label("Rent"), 0, 2);
        form.add(txtRent, 1, 2);

        form.add(new Label("Owner Name"), 0, 3);
        form.add(txtOwner, 1, 3);

        form.add(new Label("Owner Contact"), 0, 4);
        form.add(txtContact, 1, 4);

        imageView.setFitWidth(220);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Button btnUpload = new Button("Upload Image");
        Button btnUpdate = new Button("Update Rent");

        VBox left = new VBox(10, form, imageView, btnUpload, btnUpdate);

        // ===== NAVIGATION =====
        Button first = new Button("First");
        Button prev = new Button("Prev");
        Button next = new Button("Next");
        Button last = new Button("Last");

        HBox nav = new HBox(10, first, prev, next, last);
        nav.setAlignment(Pos.CENTER);

        // ===== TABLE (JOIN RESULT) =====
        TableColumn<HouseRecord, Integer> c1 =
                new TableColumn<>("House ID");
        c1.setCellValueFactory(
                new PropertyValueFactory<>("houseId"));

        TableColumn<HouseRecord, String> c2 =
                new TableColumn<>("Address");
        c2.setCellValueFactory(
                new PropertyValueFactory<>("address"));

        TableColumn<HouseRecord, Double> c3 =
                new TableColumn<>("Rent");
        c3.setCellValueFactory(
                new PropertyValueFactory<>("rent"));

        TableColumn<HouseRecord, String> c4 =
                new TableColumn<>("Owner");
        c4.setCellValueFactory(
                new PropertyValueFactory<>("owner"));

        TableColumn<HouseRecord, String> c5 =
                new TableColumn<>("Contact");
        c5.setCellValueFactory(
                new PropertyValueFactory<>("contact"));

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setPrefHeight(200);

        metaArea.setEditable(false);
        metaArea.setPrefHeight(120);

        VBox right = new VBox(
                10,
                new Label("JOIN Result (House + Owner)"),
                table,
                new Label("ResultSet MetaData"),
                metaArea
        );

        BorderPane root = new BorderPane();
        root.setLeft(left);
        root.setCenter(right);
        root.setBottom(nav);

        BorderPane.setMargin(left, new Insets(10));
        BorderPane.setMargin(right, new Insets(10));

        // ===== EVENTS =====
        first.setOnAction(e -> moveFirst());
        prev.setOnAction(e -> movePrev());
        next.setOnAction(e -> moveNext());
        last.setOnAction(e -> moveLast());

        btnUpdate.setOnAction(e -> updateRent());
        btnUpload.setOnAction(e -> uploadImage(stage));

        connectDB();

        stage.setScene(new Scene(root, 1000, 600));
        stage.setTitle("Home Rental Management System");
        stage.show();
    }

    // ===== DATABASE CONNECTION =====
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/home_rental",
                    "root",
                    "Zehrah@123"
            );

            stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );

            executeQuery();
            rs.first();
            showRecord();
            showMetaData();
            loadJoinTable();

        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    void executeQuery() throws SQLException {
        rs = stmt.executeQuery(
                "SELECT h.house_id, h.address, h.rent, " +
                "o.owner_name, o.contact, h.photo " +
                "FROM house h JOIN owner o " +
                "ON h.owner_id = o.owner_id"
        );
    }

    void showRecord() throws SQLException {
        txtId.setText(rs.getString("house_id"));
        txtAddress.setText(rs.getString("address"));
        txtRent.setText(rs.getString("rent"));
        txtOwner.setText(rs.getString("owner_name"));
        txtContact.setText(rs.getString("contact"));

        Blob b = rs.getBlob("photo");
        if (b != null)
            imageView.setImage(new Image(b.getBinaryStream()));
        else
            imageView.setImage(null);
    }

    // ===== NAVIGATION =====
    void moveFirst() {
        try { rs.first(); showRecord(); } catch (Exception ignored) {}
    }

    void movePrev() {
        try {
            if (!rs.isFirst()) rs.previous();
            showRecord();
        } catch (Exception ignored) {}
    }

    void moveNext() {
        try {
            if (!rs.isLast()) rs.next();
            showRecord();
        } catch (Exception ignored) {}
    }

    void moveLast() {
        try { rs.last(); showRecord(); } catch (Exception ignored) {}
    }

    // ===== UPDATE RENT =====
    void updateRent() {
        try {
            PreparedStatement ps =
                    con.prepareStatement(
                            "UPDATE house SET rent=? WHERE house_id=?"
                    );
            ps.setDouble(1, Double.parseDouble(txtRent.getText()));
            ps.setInt(2, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();

            executeQuery();
            rs.first();
            showRecord();
            loadJoinTable();

            showAlert("Rent updated successfully");

        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    // ===== IMAGE UPLOAD (BLOB) =====
    void uploadImage(Stage stage) {
        try {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);
            if (file == null) return;

            PreparedStatement ps =
                    con.prepareStatement(
                            "UPDATE house SET photo=? WHERE house_id=?"
                    );
            ps.setBinaryStream(
                    1,
                    new FileInputStream(file),
                    file.length()
            );
            ps.setInt(2, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();

            executeQuery();
            rs.first();
            showRecord();

            showAlert("Image stored in database");

        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    // ===== METADATA =====
    void showMetaData() throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        StringBuilder sb =
                new StringBuilder(
                        "Total Columns: " + md.getColumnCount() + "\n\n"
                );

        for (int i = 1; i <= md.getColumnCount(); i++) {
            sb.append(md.getColumnName(i))
              .append(" : ")
              .append(md.getColumnTypeName(i))
              .append("\n");
        }
        metaArea.setText(sb.toString());
    }

    // ===== LOAD JOIN TABLE =====
    void loadJoinTable() throws SQLException {
        ObservableList<HouseRecord> data =
                FXCollections.observableArrayList();

        ResultSet r = stmt.executeQuery(
                "SELECT h.house_id, h.address, h.rent, " +
                "o.owner_name, o.contact " +
                "FROM house h JOIN owner o " +
                "ON h.owner_id = o.owner_id"
        );

        while (r.next()) {
            data.add(new HouseRecord(
                    r.getInt("house_id"),
                    r.getString("address"),
                    r.getDouble("rent"),
                    r.getString("owner_name"),
                    r.getString("contact")
            ));
        }
        table.setItems(data);
    }

    void showAlert(String msg) {
        Alert a =
                new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
