package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database
{
    private static Database instance = null; // Singleton
    private final List<Product> products;

    public static Database getInstance()
    {
        if (instance == null)
            instance = new Database();

        return instance;
    }

    public Database()
    {
        products = new ArrayList<>();
    }

    public void loadProducts()
    {
        products.clear();
        // loading products from the database

        try
        {
            File file = new File("products.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine())
            {
                String[] lineSplit = scanner.nextLine().split(",");
                String code = lineSplit[0], name = lineSplit[1];
                Integer quantity = Integer.parseInt(lineSplit[2]);
                Double price = Double.parseDouble(lineSplit[3]);
                String expiryDate = lineSplit[4];
                Product product = new Product(code, name, quantity, price, expiryDate);
                products.add(product);
            }

            scanner.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Product> getProducts()
    {
        loadProducts();
        return products;
    }
}