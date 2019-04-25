package com.example.Saad.MyFYPProject.models.orders;

import java.util.ArrayList;

public class OrdersRes {
    private boolean  result;
    private ArrayList<Orders> orders = new ArrayList<>();

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Orders> orders) {
        this.orders = orders;
    }
}
