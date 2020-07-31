package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import model.Database;
import model.Product;
import view.MainView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class AddToCartController implements EventHandler<ActionEvent>
{
    Integer quantity;
    private final MainView mainView;

    public AddToCartController(MainView mainView)
    {
        this.mainView = mainView;
    }

    public Double getNewTotalPrice(Double productPrice, Integer productQuantity)
    {
        // calculate new total when adding another product to cart
        double oldTotal = Double.parseDouble(mainView.getTotalAmountLbl().getText().substring(1));
        return productPrice * productQuantity + oldTotal;
    }

    private void clearTextFields()
    {
        mainView.getProductNameOrCodeTf().clear();
        mainView.getProductQuantityTf().clear();
    }

    private Integer getQuantity()
    {
        // entered quantity in the TextField
        return (mainView.getProductQuantityTf().getText().isEmpty()) ? 1:Integer.parseInt(mainView.getProductQuantityTf().getText());
    }

    private String getProductName()
    {
        // entered name/code in the TextField
        String productName = "", name = mainView.getProductNameOrCodeTf().getText();
        if (!name.isEmpty()) productName = Character.toUpperCase(name.charAt(0)) + "";
        if (name.length() > 1) productName += name.substring(1);
        return productName;
    }

    private int getItemQuantity(String name, List<Product> products)
    {
        for (Object item: products)
        {
            Product product = (Product)item;
            if (product.getName().equals(name)) return product.getQuantity();
        }

        return 0;
    }

    private boolean isNumeric(String s)
    {
        for (int i = 0; i < s.length(); ++i)
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;

        return true; // name is actually product code
    }

    private String getNameFromCode(String code, List<Product> products)
    {
        // "converts" code to name
        for (Product product: products)
        {
            if (product.getCode().equals(code))
                return product.getName();
        }

        return "";
    }

    private boolean pastExpirationDate(String date)
    {
        if (date.equals("no expiry date")) return false;

        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = format.parse(date);
            Date date2 = format.parse(LocalDate.now().toString());
            return date1.compareTo(date2) <= 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void handle(ActionEvent actionEvent)
    {
        quantity = getQuantity(); // entered quantity or 1 by default
        String productName = getProductName(); // entered product name/code
        List<Product> products = Database.getInstance().getProducts(); // load products from the database
        if (isNumeric(productName)) productName = getNameFromCode(productName, products); // convert code to name

        ObservableList items = mainView.getCartTv().getItems(); // items in cart
        Product test = new Product();
        test.setName(productName);
        int itemQuantity = getItemQuantity(productName, products); // quantity in database
        Alert qAlert = new Alert(Alert.AlertType.ERROR, "Error! Too large quantity!");

        // check if the product is already in cart
        if (items.contains(test) && quantity > 0)
        {
            for (Object item: items)
            {
                if (((Product) item).getName().equals(productName))
                {
                    // check expiry date
                    if (pastExpirationDate(((Product) item).getExpiryDate()))
                    {
                        Alert warning = new Alert(Alert.AlertType.WARNING, "PAST EXPIRATION DATE");
                        warning.show();
                    }

                    // if it's already in cart, check the quantity
                    if (quantity + ((Product) item).getQuantity() > itemQuantity)
                    {
                        // too large quantity
                        qAlert.show();
                        return;
                    }
                    else
                    {
                        // update
                        ((Product) item).setQuantity(quantity + ((Product) item).getQuantity());
                        Double newTotal = getNewTotalPrice(((Product) item).getPrice(), quantity);
                        mainView.getTotalAmountLbl().setText("$" + String.format("%.2f", newTotal));
                        mainView.getCartTv().refresh();
                    }

                    clearTextFields();
                    return;
                }
            }

            return;
        }

        if (quantity > itemQuantity)
        {
            qAlert.show();
            return;
        }

        for (Product product: products)
        {
            if (product.getName().equals(productName) && this.quantity > 0)
            {
                if (pastExpirationDate(product.getExpiryDate()))
                {
                    Alert warning = new Alert(Alert.AlertType.WARNING, "PAST EXPIRATION DATE");
                    warning.show();
                }

                // adding product to the cart for the first time
                Product tmp = new Product(product.getCode(), productName, quantity,
                        product.getPrice(), product.getExpiryDate(), new Button("Remove"), mainView);

                items.add(tmp);
                Double newTotal = getNewTotalPrice(product.getPrice(), this.quantity);
                mainView.getTotalAmountLbl().setText("$" + String.format("%.2f", newTotal));
                clearTextFields();
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Product Name or Quantity!");
        alert.show();
    }
}