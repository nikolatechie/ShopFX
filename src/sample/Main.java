package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application
{
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("ShopFX");
        primaryStage.setScene(LoginView.makeScene());
        primaryStage.show();
        primaryStage.setResizable(false);
        Main.primaryStage = primaryStage;
    }

    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
