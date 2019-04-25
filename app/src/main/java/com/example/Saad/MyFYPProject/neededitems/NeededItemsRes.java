package com.example.Saad.MyFYPProject.neededitems;

import com.example.Saad.MyFYPProject.models.items.Item;

import java.util.ArrayList;

public class NeededItemsRes {

    private boolean result;

    private ArrayList<Item> needed_items = new ArrayList<>();

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayList<Item> getItems() {
        return needed_items;
    }

    public void setItems(ArrayList<Item> items) {
        this.needed_items = items;
    }
}
