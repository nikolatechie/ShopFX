package model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

// TextField with auto-suggestions
public class AutoCompleteTextField extends TextField
{
    private final SortedSet<String> entries;
    private final ContextMenu entriesPopup;

    public AutoCompleteTextField()
    {
        super();
        entries = new TreeSet<>(); // contains all products' names
        List <Product> products = Database.getInstance().getProducts();

        for (Product product: products)
        {
            String name = product.getName();
            entries.add(name);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            entries.add(name);
        }

        entriesPopup = new ContextMenu();

        textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1)
            {
                if (getText().length() == 0)
                    entriesPopup.hide();
                else
                {
                    // get subset of suggested words
                    LinkedList<String> searchResult = new LinkedList<>(entries.subSet(getText(), getText() + Character.MAX_VALUE));

                    if (entries.size() > 0)
                    {
                        populatePopup(searchResult);

                        if (!entriesPopup.isShowing())
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                    else entriesPopup.hide(); // no suggestions
                }
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                entriesPopup.hide();
            }
        });
    }

    private void populatePopup(List<String> searchResult)
    {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int maxEntries = 4;
        int count = Math.min(searchResult.size(), maxEntries);

        for (int i = 0; i < count; ++i)
        {
            final String result = searchResult.get(i);
            Label entryLbl = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLbl, true);

            item.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
                    setText(result); // set TextField text to a selected suggested name
                    entriesPopup.hide();
                }
            });

            menuItems.add(item);
        }

        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}