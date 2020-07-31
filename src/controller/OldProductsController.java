package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.Main;
import view.MainView;
import view.OldProductsView;

public class OldProductsController implements EventHandler<ActionEvent>
{
    private final LoginController loginController;
    private final MainView mainView;

    public OldProductsController(LoginController loginController, MainView mainView)
    {
        this.loginController = loginController;
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent)
    {
        Main.getPrimaryStage().setScene(OldProductsView.makeScene(loginController, mainView));
    }
}