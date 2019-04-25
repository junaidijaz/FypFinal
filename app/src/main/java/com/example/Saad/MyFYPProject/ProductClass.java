package com.example.Saad.MyFYPProject;

public class ProductClass {
    private String name;
    private int price;
    private int quantity;
    private int SubTotal;

    ProductClass(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        SubTotal = price*quantity;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSubTotal() {
        return SubTotal;
    }
}
