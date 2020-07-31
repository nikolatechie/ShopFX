package model;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import view.AllProductsView;
import view.MainView;
import view.OldProductsView;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Product
{
    private String code, name;
    private Integer quantity;
    private Double price;
    private String expiryDate;
    private Button removeBtn, removeFromDbAPV, removeFromDbOPV;
    private MainView mainView;
    private OldProductsView oldProductsView;
    private AllProductsView allProductsView;

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Product && name.equals(((Product) obj).getName());
    }

    public Product() {}

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public Double getPrice()
    {
        return price;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public Button getRemoveBtn()
    {
        return removeBtn;
    }

    public Button getRemoveFromDbAPV()
    {
        return removeFromDbAPV;
    }

    public void setRemoveFromDbAPV(Button removeFromDbAPV)
    {
        this.removeFromDbAPV = removeFromDbAPV;
        this.removeFromDbAPV.setOnAction(handleRemoveFromDbAPV());
    }

    public Button getRemoveFromDbOPV()
    {
        return removeFromDbOPV;
    }

    public void setRemoveFromDbOPV(Button removeFromDbOPV)
    {
        this.removeFromDbOPV = removeFromDbOPV;
        this.removeFromDbOPV.setOnAction(handleRemoveFromDbOPV());
    }

    public Product(String code, String name, Integer quantity, Double price, String expiryDate)
    {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public Product(String code, String name, Integer quantity, Double price,
                   String expiryDate, Button removeBtn, MainView mainView)
    {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.removeBtn = removeBtn;
        this.mainView = mainView;
        removeBtn.setOnAction(handleRemoveBtn());
    }

    public OldProductsView getOldProductsView()
    {
        return oldProductsView;
    }

    public void setOldProductsView(OldProductsView oldProductsView)
    {
        this.oldProductsView = oldProductsView;
    }

    public AllProductsView getAllProductsView()
    {
        return allProductsView;
    }

    public void setAllProductsView(AllProductsView allProductsView)
    {
        this.allProductsView = allProductsView;
    }

    private EventHandler<ActionEvent> handleRemoveBtn()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                // remove product from cart
                ObservableList items = mainView.getCartTv().getItems();
                double price1 = 0.0;

                for (Object product: items)
                {
                    Product item = (Product)product;

                    if (item.getName().equals(name))
                    {
                        price1 = item.getQuantity() * item.getPrice();
                        items.remove(product);
                        break;
                    }
                }

                double totalPrice = Double.parseDouble(mainView.getTotalAmountLbl().getText().substring(1));
                totalPrice -= price1;
                mainView.getTotalAmountLbl().setText("$" + String.format("%.2f", totalPrice));
                mainView.getCartTv().refresh();
            }
        };
    }

    @Override
    public String toString()
    {
        return code + "," + name + "," + quantity + "," + price + "," + expiryDate + "\n";
    }

    public void removeFromDatabase(List<String> names)
    {
        try
        {
            List<Product> allProducts = Database.getInstance().getProducts();
            FileWriter file = new FileWriter("products.txt");

            for (Product product: allProducts)
            {
                if (!names.contains(product.getName()))
                    file.write(product.toString());
            }

            file.close();
            // if the product was in cart, remove it
            ObservableList items = mainView.getCartTv().getItems();
            double subtract = 0.0;

            for (String name: names)
            {
                for (Object product: items)
                {
                    Product item = (Product)product;

                    if (item.getName().equals(name))
                    {
                        items.remove(item);
                        subtract += item.getQuantity() * item.getPrice();
                        break;
                    }
                }
            }

            mainView.getCartTv().refresh();
            double total = Double.parseDouble(mainView.getTotalAmountLbl().getText().substring(1));
            total -= subtract;
            mainView.getTotalAmountLbl().setText("$" + String.format("%.2f", total));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setMainView(MainView mainView)
    {
        this.mainView = mainView;
    }

    private EventHandler<ActionEvent> handleRemoveFromDbOPV()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ObservableList items = oldProductsView.getOldProductsTv().getItems();

                for (Object product: items)
                {
                    Product item = (Product)product;

                    if (item.getName().equals(name))
                    {
                        List<String> list = new ArrayList<String>();
                        list.add(item.getName());
                        removeFromDatabase(list);
                        items.remove(item);
                        oldProductsView.getOldProductsTv().refresh();
                        return;
                    }
                }
            }
        };
    }

    private EventHandler<ActionEvent> handleRemoveFromDbAPV()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ObservableList items = allProductsView.getAllProductsTv().getItems();

                for (Object product: items)
                {
                    Product item = (Product)product;

                    if (item.getName().equals(name))
                    {
                        List<String> list = new ArrayList<String>();
                        list.add(item.getName());
                        removeFromDatabase(list);
                        items.remove(item);
                        allProductsView.getAllProductsTv().refresh();
                        return;
                    }
                }
            }
        };
    }
}