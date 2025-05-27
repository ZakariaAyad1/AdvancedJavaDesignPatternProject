package techstore.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import techstore.MainApp;
import techstore.model.User;
import techstore.patterns.factory.UserFactory;
import techstore.patterns.singleton.UserManager;

import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private MainApp mainApp;
    private UserManager userManager;
    private UserFactory userFactory;

    public LoginController() {
        userManager = UserManager.getInstance(); // Singleton
        userFactory = new UserFactory();       // Factory
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Optional<User> userOpt = userManager.findUserByUsername(username);
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            messageLabel.setText("");
            mainApp.showMainView(userOpt.get());
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegisterClient() {
        // Pour une vraie UI de registration, un nouveau dialogue/fenêtre serait mieux
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Client Registration");
        usernameDialog.setHeaderText("Enter New Client Details");
        usernameDialog.setContentText("Username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();

        usernameResult.ifPresent(username -> {
            if (userManager.findUserByUsername(username).isPresent()) {
                showAlert(AlertType.ERROR, "Registration Failed", "Username already exists.");
                return;
            }

            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setContentText("Password:");
            Optional<String> passwordResult = passwordDialog.showAndWait();

            passwordResult.ifPresent(password -> {
                TextInputDialog emailDialog = new TextInputDialog();
                emailDialog.setContentText("Email:");
                Optional<String> emailResult = emailDialog.showAndWait();

                emailResult.ifPresent(email -> {
                    User newClient = userFactory.createUser(UserFactory.UserType.CLIENT, username, password, email);
                    userManager.addUser(newClient); // Observer Pattern notifiera
                    showAlert(AlertType.INFORMATION, "Registration Successful", "Client " + username + " registered.");
                });
            });
        });
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}