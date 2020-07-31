package view;

import controller.LogOutController;
import controller.LoginController;
import controller.MainScreenController;
import controller.ProductSearchController;
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

import java.util.ArrayList;
import java.util.List;

public class AllProductsView extends VBox
{
    private Label employeeLbl, employeeNameLbl, searchProductLbl;
    private Button logOutBtn, removeSelectedBtn, mainScreenBtn;
    private TableView allProductsTv;
    private TextField searchTf;
    private static LoginController loginController;
    private static MainView mainView;

    public AllProductsView()
    {
        initElements();
        placeElements();
        initControllers();
    }

    private void initElements()
    {
        employeeLbl = new Label("Employee:");
        employeeNameLbl = new Label(loginController.getEmployeeName());
        searchProductLbl = new Label("Search:");
        logOutBtn = new Button("Log out");
        mainScreenBtn = new Button("Main screen");
        searchTf = new TextField();
        searchTf.setPromptText("Product name");
        removeSelectedBtn = new Button("Remove selected items");

        allProductsTv = new TableView();
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
        removeProductCol.setCellValueFactory(new PropertyValueFactory<Product, Button>("removeFromDbAPV"));
        allProductsTv.getColumns().addAll(productNameCol, priceCol, productQuantityCol, productCodeCol, expiryDateCol, removeProductCol);
        List<Product> products = Database.getInstance().getProducts();

        // add all products from database to TableView
        for (Product product: products)
        {
            product.setRemoveFromDbAPV(new Button("Remove"));
            product.setAllProductsView(this);
            product.setMainView(mainView);
        }

        allProductsTv.getItems().clear();
        allProductsTv.getItems().addAll(products);
        allProductsTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        HBox productSearch = new HBox(20);
        searchProductLbl.setPadding(new Insets(0, 0, 0, 50));
        productSearch.setAlignment(Pos.CENTER_LEFT);
        productSearch.getChildren().addAll(searchProductLbl, searchTf);

        HBox tableRow = new HBox(20);
        allProductsTv.setPrefSize(800, 400);
        tableRow.setAlignment(Pos.CENTER);
        tableRow.getChildren().add(allProductsTv);

        this.getChildren().addAll(firstRow, productSearch, tableRow);
        this.setSpacing(20);
    }

    private void initControllers()
    {
        logOutBtn.setOnAction(new LogOutController());
        mainScreenBtn.setOnAction(new MainScreenController());
        searchTf.setOnKeyTyped(new ProductSearchController(this));

        // remove selected items from TableView
        removeSelectedBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ObservableList selectedItems = allProductsTv.getSelectionModel().getSelectedItems();
                List<String> names = new ArrayList<>();

                for (Object item: selectedItems)
                {
                    Product selectedItem = (Product)item;
                    names.add(selectedItem.getName());
                }

                Product dummy = new Product();
                dummy.setMainView(mainView);
                dummy.removeFromDatabase(names);
                allProductsTv.getItems().removeAll(selectedItems);
                allProductsTv.refresh();
            }
        });
    }

    public String getSearchTfText()
    {
        return searchTf.getText();
    }

    public TableView getAllProductsTv()
    {
        return allProductsTv;
    }

    public static Scene makeScene(LoginController loginController, MainView mainView)
    {
        AllProductsView.loginController = loginController;
        AllProductsView.mainView = mainView;
        Scene scene = new Scene(new AllProductsView(), 840, 520);
        scene.getStylesheets().add("style.css");
        return scene;
    }
}