package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import sample.Main;
import view.MainView;

public class MainScreenController implements EventHandler<ActionEvent>
{
    @Override
    public void handle(ActionEvent actionEvent)
    {
        Main.getPrimaryStage().setScene(MainView.getCreatedScene()); // shows saved state of MainView
    }
}