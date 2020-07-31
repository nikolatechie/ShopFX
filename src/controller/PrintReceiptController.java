package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.Database;
import model.Product;
import view.MainView;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

public class PrintReceiptController implements EventHandler<ActionEvent>
{
    private final MainView mainView;

    public PrintReceiptController(MainView mainView)
    {
        this.mainView = mainView;
    }

    private void printReceipt()
    {
        // prints receipt to a text field in a preferred format
        try
        {
            FileWriter file = new FileWriter("receipt.txt");
            file.write("RECEIPT " + LocalDate.now().toString() + "\n\nBought products:\n");
            ObservableList items = mainView.getCartTv().getItems();
            int i = 1;
            double totalPrice = 0.0;

            for (Object itemIterator: items)
            {
                Product item = (Product)itemIterator;
                file.write(i + ". " + item.getName() + " -> " + item.getQuantity().toString()
                        + "x " + String.format("%.2f", item.getPrice()) + " => ("
                        + String.format("%.2f", item.getPrice() * item.getQuantity()) + ")\n");

                totalPrice += item.getQuantity() * item.getPrice();
                ++i;
            }

            file.write("\nTotal price: [" + String.format("%.2f", totalPrice) + "]");
            file.close();
            mainView.getCartTv().getItems().clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateDatabase()
    {
        try
        {
            List<Product> products = Database.getInstance().getProducts();
            ObservableList items = mainView.getCartTv().getItems();

            for (Object itemIterator: items)
            {
                Product item = (Product)itemIterator;

                for (Product product: products)
                {
                    if (item.getName().equals(product.getName()))
                    {
                        // removes bought items and updates their quantity in the database
                        product.setQuantity(product.getQuantity() - item.getQuantity());
                        break;
                    }
                }
            }

            FileWriter file = new FileWriter("products.txt");

            for (Product product: products)
            {
                // removes from database products whose quantity is 0
                if (product.getQuantity() > 0)
                    file.write(product.toString());
            }

            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ActionEvent actionEvent)
    {
        if (mainView.isCartEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No items in cart!");
            alert.show();
            return;
        }

        updateDatabase();
        printReceipt();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The receipt has been printed!");
        alert.setHeaderText("Receipt");
        alert.show();
    }
}