package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.Main;
import view.AllProductsView;
import view.MainView;

public class AllProductsController implements EventHandler<ActionEvent>
{
    private final LoginController loginController;
    private final MainView mainView;

    public AllProductsController(LoginController loginController, MainView mainView)
    {
        this.loginController = loginController;
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent)
    {
        Main.getPrimaryStage().setScene(AllProductsView.makeScene(loginController, mainView));
    }
}