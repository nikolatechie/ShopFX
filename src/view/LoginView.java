package view;

import controller.LoginController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LoginView extends VBox
{
    private Label employeeLbl;
    private TextField employeeTf;
    private PasswordField employeePf;
    private Button loginBtn;

    public static Scene makeScene()
    {
        Scene scene = new Scene(new LoginView(), 840, 520);
        scene.getStylesheets().add("style.css");
        return scene;
    }

    private void initElements()
    {
        employeeLbl = new Label("Employee");
        employeeTf = new TextField();
        employeeTf.setPromptText("Full name");
        employeePf = new PasswordField();
        employeePf.setPromptText("Password");
        loginBtn = new Button("Log in");
        loginBtn.setId("loginBtn");
    }

    private void placeElements()
    {
        employeeLbl.setStyle("-fx-font-size: 32px");

        employeeTf.setPrefWidth(250);
        employeeTf.setPrefHeight(50);
        employeeTf.setFont(new Font(14));
        employeePf.setPrefWidth(250);
        employeePf.setPrefHeight(50);
        employeePf.setFont(new Font(14));

        HBox nameHb = new HBox();
        nameHb.getChildren().add(employeeTf);
        nameHb.setAlignment(Pos.CENTER);
        
        HBox passHb = new HBox();
        passHb.getChildren().add(employeePf);
        passHb.setAlignment(Pos.CENTER);

        loginBtn.setPrefWidth(270);
        loginBtn.setPrefHeight(50);
        loginBtn.setFont(new Font(22));

        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(employeeLbl, nameHb, passHb, loginBtn);
    }

    private void initControllers()
    {
        loginBtn.setOnAction(new LoginController(this));
    }

    public LoginView()
    {
        initElements();
        placeElements();
        initControllers();
    }

    public TextField getEmployeeTf()
    {
        return employeeTf;
    }

    public PasswordField getPasswordPf()
    {
        return employeePf;
    }
}