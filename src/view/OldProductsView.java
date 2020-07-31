package view;

import controller.LogOutController;
import controller.LoginController;
import controller.MainScreenController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Database;
import model.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OldProductsView extends VBox
{
    private Label employeeLbl, employeeNameLbl, oldProductsLbl;
    private Button logOutBtn, mainScreenBtn, removeSelectedBtn;
    private TableView oldProductsTv;
    private static LoginController loginController;
    private static MainView mainView;

    public OldProductsView()
    {
        initElements();
        placeElements();
        initControllers();
    }

    private void initElements()
    {
        employeeLbl = new Label("Employee:");
        employeeNameLbl = new Label(loginController.getEmployeeName());
        oldProductsLbl = new Label("Old products:");
        logOutBtn = new Button("Log out");
        mainScreenBtn = new Button("Main screen");
        removeSelectedBtn = new Button("Remove selected items");

        oldProductsTv = new TableView();
        TableColumn productNameCol = new TableColumn("Product name");
        productNameCol.setPrefWidth(230);
        productNameCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn priceCol = new TableColumn("Price");
        priceCol.setPrefWidth(130);
        priceCol.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        TableColumn productQuantityCol = new TableColumn("Quantity");
        productQuantityCol.setPrefWidth(90);
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

        TableColumn productCodeCol = new TableColumn("Code");
        productCodeCol.setPrefWidth(100);
        productCodeCol.setCellValueFactory(new PropertyValueFactory<Product, String>("code"));

        TableColumn expiryDateCol = new TableColumn("Expiry Date");
        expiryDateCol.setPrefWidth(110);
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<Product, String>("expiryDate"));

        TableColumn removeProductCol = new TableColumn("Remove product");
        removeProductCol.setPrefWidth(140);
        removeProductCol.setCellValueFactory(new PropertyValueFactory<Product, Button>("removeFromDbOPV"));
        oldProductsTv.getColumns().addAll(productNameCol, priceCol, productQuantityCol, productCodeCol, expiryDateCol, removeProductCol);

        List<Product> products = Database.getInstance().getProducts();

        for (Product product: products)
        {
            product.setRemoveFromDbOPV(new Button("Remove"));
            product.setOldProductsView(this);
            product.setMainView(mainView);
        }

        products = products.stream().filter
        ( product ->
        {
            if (product.getExpiryDate().equals("no expiry date")) return false;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try
            {
                Date date1 = format.parse(product.getExpiryDate());
                Date date2 = format.parse(LocalDate.now().toString());
                return date1.compareTo(date2) <= 0;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            return false;
        })
        .collect(Collectors.toList());

        // adding only "old" products from the database to the TableView (filtered above)
        oldProductsTv.getItems().clear();
        oldProductsTv.getItems().addAll(products);
        oldProductsTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void placeElements()
    {
        HBox topLeft = new HBox(10);
        topLeft.setAlignment(Pos.CENTER);
        topLeft.getChildren().addAll(employeeLbl, employeeNameLbl, logOutBtn);
        HBox topRight = new HBox(20);
        topRight.setAlignment(Pos.CENTER);
        topRight.getChildren().addAll(removeSelectedBtn, mainScreenBtn);
        BorderPane firstRow = new BorderPane();
        firstRow.setLeft(topLeft);
        firstRow.setRight(topRight);
        firstRow.setPadding(new Insets(10));

        oldProductsLbl.setPadding(new Insets(0, 0, 0, 50));

        HBox tableRow = new HBox(20);
        oldProductsTv.setPrefSize(800, 400);
        tableRow.setAlignment(Pos.CENTER);
        tableRow.getChildren().add(oldProductsTv);

        this.getChildren().addAll(firstRow, oldProductsLbl, tableRow);
        this.setSpacing(20);
    }

    private void initControllers()
    {
        logOutBtn.setOnAction(new LogOutController());
        mainScreenBtn.setOnAction(new MainScreenController());

        // remove selected items from TableView
        removeSelectedBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ObservableList selectedItems = oldProductsTv.getSelectionModel().getSelectedItems();
                List<String> names = new ArrayList<>();

                for (Object item: selectedItems)
                {
                    Product selectedItem = (Product)item;
                    names.add(selectedItem.getName());
                }

                Product dummy = new Product();
                dummy.setMainView(mainView);
                dummy.removeFromDatabase(names);
                oldProductsTv.getItems().removeAll(selectedItems);
                oldProductsTv.refresh();
            }
        });
    }

    public TableView getOldProductsTv()
    {
        return oldProductsTv;
    }

    public static Scene makeScene(LoginController loginController, MainView mainView)
    {
        OldProductsView.loginController = loginController;
        OldProductsView.mainView = mainView;
        Scene scene = new Scene(new OldProductsView(), 840, 520);
        scene.getStylesheets().add("style.css");
        return scene;
    }
}