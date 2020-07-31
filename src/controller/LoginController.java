package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import sample.Main;
import view.LoginView;
import view.MainView;

import java.io.File;
import java.util.Scanner;

public class LoginController implements EventHandler<ActionEvent>
{
    private final LoginView loginView;
    private String employeeName;

    public LoginController(LoginView loginView)
    {
        this.loginView = loginView;
    }

    public String getEmployeeName()
    {
        return employeeName;
    }

    private boolean correctCombination(String hashName, String hashPass)
    {
        // check if there is a user whose hashed name
        // is hashName and hashed password is hashPass
        try
        {
            File file = new File("employees.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine())
            {
                String[] lineSplit = scanner.nextLine().split(",");
                if (lineSplit[0].equals(hashName) && lineSplit[1].equals(hashPass)) return true;
            }

            scanner.close();
        }
        catch (Exception e)
        {
            return false;
        }

        return false;
    }

    @Override
    public void handle(ActionEvent actionEvent)
    {
        employeeName = loginView.getEmployeeTf().getText(); // entered name
        String employeePass = loginView.getPasswordPf().getText(); // enter pass
        Integer nameHash = employeeName.hashCode();
        Integer passHash = employeePass.hashCode();
        String hashName = nameHash.toString();
        String hashPass = passHash.toString();

        if (correctCombination(hashName, hashPass))
            Main.getPrimaryStage().setScene(MainView.makeScene(this));
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wrong name and/or password!");
            alert.show();
        }
    }
}