package view;

import controller.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.AutoCompleteTextField;
import model.Product;

public class MainView extends VBox
{
    private Label employeeLbl, employeeNameLbl, productNameOrCodeLbl, productQuantityLbl, totalLbl, totalAmountLbl;
    private Button logOutBtn, removeSelectedBtn, emptyCartBtn, oldProductsBtn, allProductsBtn, addBtn, printBtn;
    private AutoCompleteTextField productNameOrCodeTf;
    private TextField productQuantityTf;
    private TableView cartTv;
    private static LoginController loginController;
    private static Scene scene;

    private void initElements()
    {
        employeeLbl = new Label("Employee:");
        employeeNameLbl = new Label(loginController.getEmployeeName());
        logOutBtn = new Button("Log out");
        removeSelectedBtn = new Button("Remove selected items");
        emptyCartBtn = new Button("Empty cart");
        oldProductsBtn = new Button("Old products");
        allProductsBtn = new Button("All products");
        productNameOrCodeLbl = new Label("Product name/code:");
        productNameOrCodeTf = new AutoCompleteTextField();
        productQuantityLbl = new Label("Quantity:");
        productQuantityTf = new TextField();
        addBtn = new Button("Add");

        cartTv = new TableView();
        TableColumn productNameCol = new TableColumn("Product name");
        productNameCol.setPrefWidth(300);
        productNameCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn productQuantityCol = new TableColumn("Quantity");
        productQuantityCol.setPrefWidth(160);
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));

        TableColumn priceCol = new TableColumn("Price");
        priceCol.setPrefWidth(140);
        priceCol.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        TableColumn removeProductCol = new TableColumn("Remove product");
        removeProductCol.setPrefWidth(200);
        removeProductCol.setCellValueFactory(new PropertyValueFactory<Product, Button>("removeBtn"));

        cartTv.getColumns().addAll(productNameCol, productQuantityCol, priceCol, removeProductCol);
        cartTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        totalLbl = new Label("Total:");
        totalAmountLbl = new Label("$0.00");
        printBtn = new Button("Print receipt");
    }

    private void placeElements()
    {
        BorderPane firstRow = new BorderPane();
        firstRow.setPadding(new Insets(10));
        HBox topLeft = new HBox(10);
        topLeft.setAlignment(Pos.CENTER_LEFT);
        topLeft.getChildren().addAll(employeeLbl, employeeNameLbl, logOutBtn);
        HBox topRight = new HBox(20);
        topRight.getChildren().addAll(oldProductsBtn, allProductsBtn, removeSelectedBtn, emptyCartBtn);
        firstRow.setLeft(topLeft);
        firstRow.setRight(topRight);

        HBox secondRow = new HBox(15);
        secondRow.setAlignment(Pos.CENTER);
        secondRow.getChildren().addAll(productNameOrCodeLbl, productNameOrCodeTf, productQuantityLbl, productQuantityTf, addBtn);

        HBox tableRow = new HBox(20);
        cartTv.setPrefSize(800, 300);
        tableRow.setAlignment(Pos.CENTER);
        tableRow.setPadding(new Insets(15, 0, 0, 0));
        tableRow.getChildren().add(cartTv);

        HBox bottomLeft = new HBox(10);
        bottomLeft.getChildren().addAll(totalLbl, totalAmountLbl);
        bottomLeft.setAlignment(Pos.CENTER);
        HBox bottomRow = new HBox(100);
        bottomRow.getChildren().addAll(bottomLeft, printBtn);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(firstRow, secondRow, tableRow, bottomRow);
        this.setSpacing(20);
    }

    private void initControllers()
    {
        allProductsBtn.setOnAction(new AllProductsController(loginController, this));
        oldProductsBtn.setOnAction(new OldProductsController(loginController, this));
        logOutBtn.setOnAction(new LogOutController());
        addBtn.setOnAction(new AddToCartController(this));
        printBtn.setOnAction(new PrintReceiptController(this));

        emptyCartBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                cartTv.getItems().clear();
                cartTv.refresh();
                totalAmountLbl.setText("$0.00");
            }
        });

        // remove selected items from cart and update the total price value
        removeSelectedBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ObservableList selectedItems = cartTv.getSelectionModel().getSelectedItems();
                double subtract = 0.0;

                for (Object item: selectedItems)
                {
                    Product selectedItem = (Product)item;
                    subtract += selectedItem.getPrice() * selectedItem.getQuantity();
                }

                cartTv.getItems().removeAll(selectedItems);
                double total = Double.parseDouble(totalAmountLbl.getText().substring(1));
                total -= subtract;
                totalAmountLbl.setText("$" + String.format("%.2f", total));
            }
        });
    }

    public boolean isCartEmpty()
    {
        return cartTv.getItems().isEmpty();
    }

    public Label getTotalAmountLbl()
    {
        return totalAmountLbl;
    }

    public TextField getProductNameOrCodeTf()
    {
        return productNameOrCodeTf;
    }

    public TextField getProductQuantityTf()
    {
        return productQuantityTf;
    }

    public TableView getCartTv()
    {
        return cartTv;
    }

    public MainView()
    {
        initElements();
        placeElements();
        initControllers();
    }

    public static Scene makeScene(LoginController loginController)
    {
        MainView.loginController = loginController;
        MainView.scene = new Scene(new MainView(), 840, 520);
        MainView.scene.getStylesheets().add("style.css");
        return scene;
    }

    public static Scene getCreatedScene()
    {
        return scene;
    }
}