package com.planner.view;

import com.planner.db.UserDAO;
import com.planner.model.User;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import java.util.function.Consumer;

public class UserAuthView {
    private final UserDAO userDAO;
    private final Consumer<User> onLoginSuccess;

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
        Label status = new Label();

        loginBtn.setOnAction(e -> {
            try {
                var user = userDAO.login(usernameField.getText(), passwordField.getText());
                if (user.isPresent()) {
                    status.setText("✅ Login success");
                    onLoginSuccess.accept(user.get());
                    stage.close();
                } else {
                    status.setText("❌ Invalid credentials");
                }
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> {
            try {
                if (userDAO.register(usernameField.getText(), passwordField.getText())) {
                    status.setText("✅ Registered! Please login");
                } else {
                    status.setText("❌ Registration failed");
                }
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setPrefHeight(300); // Allign center
        box.setPrefWidth(300);
        box.getChildren().addAll(usernameField, passwordField, loginBtn, registerBtn, status);

        stage.setScene(new Scene(box));
        stage.setTitle("Login/Register");
        stage.show();
    }
}