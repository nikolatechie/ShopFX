package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.Main;
import view.LoginView;

public class LogOutController implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent actionEvent)
    {
        Main.getPrimaryStage().setScene(LoginView.makeScene());
    }
}