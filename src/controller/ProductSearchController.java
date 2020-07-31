package controller;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import model.Database;
import model.Product;
import view.AllProductsView;

import java.util.List;
import java.util.stream.Collectors;

public class ProductSearchController implements EventHandler<KeyEvent>
{
    private final AllProductsView allProductsView;

    public ProductSearchController(AllProductsView allProductsView)
    {
        this.allProductsView = allProductsView;
    }

    @Override
    public void handle(KeyEvent keyEvent)
    {
        // when new character is typed, only show
        // products whose name prefix is equal to
        // the entered prefix in the TextField
        String prefix = allProductsView.getSearchTfText();
        TableView allProductsTv = allProductsView.getAllProductsTv();

        List<Product> products = Database.getInstance().getProducts().
            stream().filter(product ->
        {
            if (product.getName().length() < prefix.length()) return false;
            if (prefix.isEmpty()) return true;

            if (Character.toLowerCase(prefix.charAt(0)) != Character.toLowerCase(product.getName().charAt(0))) return false;

            for (int i = 1; i < prefix.length(); ++i)
                if (prefix.charAt(i) != product.getName().charAt(i)) return false;

            return true;
        })
        .collect(Collectors.toList());

        for (Product product: products)
        {
            product.setRemoveFromDbAPV(new Button("Remove"));
            product.setAllProductsView(allProductsView);
        }

        allProductsTv.getItems().clear();
        allProductsTv.getItems().addAll(products);
        allProductsTv.refresh();
    }
}