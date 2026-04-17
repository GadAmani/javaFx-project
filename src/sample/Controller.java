package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {

    // ===================== PRODUCT TABLE =====================
    @FXML private TableView<Product> productTable;

    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Void> editColumn;
    @FXML private TableColumn<Product, Void> deleteColumn;
    @FXML
    private Button addProductButton;
    @FXML
    private TextField incrementField;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    // ===================== LOGIN =====================
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordPasswordField;
    @FXML private Label loginMessageLabel;
    @FXML private Button cancelButton;

    // ===================== WELCOME =====================
    @FXML private Label welcomeUsername;
    @FXML private Label welcomeUsername1;

    public static String currentUser;

    // ===================== REGISTER =====================
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label registerMessage;

    // ===================== INITIALIZE =====================
    @FXML
    public void initialize() {

        // LOGIN/REGISTER pages may not have table → avoid crash
        if (productTable != null) {

            nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
            quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
            priceColumn.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

            productTable.setItems(productList);
            productTable.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);

                    if (empty || product == null) {
                        setStyle("");
                    } else if (product.getQuantity() == 0) {
                        setStyle("-fx-background-color: #ffb3b3;");
                    } else {
                        setStyle("");
                    }
                }
            });

            setupActionButtons();
            loadProductsFromDatabase();
        }
    }

    // ===================== LOGOUT =====================
    public void logoutButtonOnAction(ActionEvent e) {
        try {
            currentUser = null;

            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ===================== LOGIN =====================
    public void loginButtonOnAction(ActionEvent e) {

        if (usernameTextField.getText().isBlank() ||
                passwordPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("Please enter username and password!");
            return;
        }

        validateLogin(e);
    }

    public void validateLogin(ActionEvent e) {

        try {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();

            String verifyLogin =
                    "SELECT COUNT(1) FROM userAccounts WHERE Name = ? AND Password = ?";

            PreparedStatement ps = connectDB.prepareStatement(verifyLogin);
            ps.setString(1, usernameTextField.getText());
            ps.setString(2, passwordPasswordField.getText());

            ResultSet queryresult = ps.executeQuery();

            if (queryresult.next() && queryresult.getInt(1) == 1) {

                currentUser = usernameTextField.getText();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
                Parent root = loader.load();

                Controller controller = loader.getController();
                controller.setUsername(currentUser);

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                loginMessageLabel.setText("Invalid login. Please try again!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ===================== WELCOME =====================
    public void setUsername(String username) {
        if (welcomeUsername != null) welcomeUsername.setText(username);
        if (welcomeUsername1 != null) welcomeUsername1.setText(username);
        currentUser = username;
    }

    // ===================== REGISTER =====================
    public void RegisterUser(ActionEvent e) {

        String UserName = registerUsernameField.getText().trim();
        String Password = registerPasswordField.getText().trim();
        String CPassword = registerConfirmPasswordField.getText().trim();

        if (UserName.isEmpty() || Password.isEmpty() || CPassword.isEmpty()) {
            registerMessage.setText("Please fill all fields!");
            return;
        }

        if (!Password.equals(CPassword)) {
            registerMessage.setText("Passwords do not match!");
            return;
        }

        try {
            DatabaseConnection ConnectNow = new DatabaseConnection();
            Connection connectDB = ConnectNow.getConnection();

            String insertQuery =
                    "INSERT INTO userAccounts (Name, Password) VALUES (?, ?)";

            PreparedStatement ps = connectDB.prepareStatement(insertQuery);

            ps.setString(1, UserName);
            ps.setString(2, Password);

            ps.executeUpdate();

            registerMessage.setText("Successfully registered!");

        } catch (SQLException ex) {
            ex.printStackTrace();
            registerMessage.setText("Registration failed!");
        }
    }

    // ===================== NAVIGATION =====================
    public void registerButton(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void haveAnAccountButton(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ===================== DATABASE =====================
    private void loadProductsFromDatabase() {

        productList.clear();

        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();

            String sql = "SELECT * FROM products";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                );

                productList.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Products loaded: " + productList.size());
        productTable.setItems(productList);
    }

    public void addProduct(String name, int quantity, double price) {

        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();

            String sql = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setDouble(3, price);

            ps.executeUpdate();

            loadProductsFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(Product product) {

        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();

            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, product.getId());

            ps.executeUpdate();

            loadProductsFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== TABLE BUTTONS =====================
    private void setupActionButtons() {

        // DELETE BUTTON COLUMN
        if (deleteColumn != null) {

            deleteColumn.setCellFactory(col -> new TableCell<>() {

                private final Button deleteBtn = new Button("Delete");

                {
                    deleteBtn.setOnAction(e -> {
                        Product p = getTableView().getItems().get(getIndex());
                        deleteProduct(p);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            });
        }

        // EDIT BUTTON COLUMN
        if (editColumn != null) {

            editColumn.setCellFactory(col -> new TableCell<>() {

                private final Button editBtn = new Button("Edit");

                {
                    editBtn.setOnAction(e -> {

                        Product p = getTableView().getItems().get(getIndex());

                        // remember which product is being edited
                        productBeingEdited = p;

                        // put values into text fields
                        nameField.setText(p.getName());
                        quantityField.setText(String.valueOf(p.getQuantity()));
                        priceField.setText(String.valueOf(p.getPrice()));

                        // transform the Add button into an Edit button
                        addProductButton.setText("Save Edit");
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : editBtn);
                }
            });
        }
    }
    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;

    private Product productBeingEdited = null;


    public void addProduct(ActionEvent e) {

        try {
            String name = nameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (name.isEmpty()) {
                System.out.println("Name required");
                return;
            }

            // if no product is currently being edited, add a new one
            if (productBeingEdited == null) {

                addProduct(name, quantity, price);

            } else {

                updateProduct(productBeingEdited.getId(), name, quantity, price);

                // leave edit mode
                productBeingEdited = null;
                addProductButton.setText("+ Add Product");
            }

            loadProductsFromDatabase();

            nameField.clear();
            quantityField.clear();
            priceField.clear();

        } catch (NumberFormatException ex) {
            System.out.println("Quantity and Price must be numbers");
        }
    }

    private void updateProduct(int id, String name, int quantity, double price) {

        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();

            String sql = "UPDATE products SET name = ?, quantity = ?, price = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setDouble(3, price);
            ps.setInt(4, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}