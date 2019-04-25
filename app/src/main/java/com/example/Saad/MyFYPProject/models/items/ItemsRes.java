package com.example.Saad.MyFYPProject.models.items;

import java.util.ArrayList;

public class ItemsRes {
    private boolean result;

    private ArrayList<Item> items = new ArrayList<>();

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
