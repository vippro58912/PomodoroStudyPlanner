package com.planner.view;

import com.planner.db.UserDAO;
import com.planner.model.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class UserAuthView {
    private UserDAO userDAO;
    private Consumer<User> onLoginSuccess;

    public UserAuthView(UserDAO userDAO, Consumer<User> onLoginSuccess) {
        this.userDAO = userDAO;
        this.onLoginSuccess = onLoginSuccess;
    }

    public void show(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        Label statusLabel = new Label();

        loginBtn.setOnAction(e -> {
            try {
                var user = userDAO.loginUser(usernameField.getText(), passwordField.getText());
                if (user.isPresent()) {
                    statusLabel.setText("✅ Logged in!");
                    onLoginSuccess.accept(user.get());
                    stage.close();
                } else {
                    statusLabel.setText("❌ Wrong credentials");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> {
            try {
                if (userDAO.registerUser(usernameField.getText(), passwordField.getText())) {
                    statusLabel.setText("✅ Registered! You can now log in.");
                } else {
                    statusLabel.setText("❌ Registration failed.");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(10, usernameField, passwordField, loginBtn, registerBtn, statusLabel);
        layout.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Login/Register");
        stage.show();
    }
}
