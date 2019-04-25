package com.example.Saad.MyFYPProject;

public class OrderDetailsClass {
   private int id;
   private String address;
   private String grandTotal;
   private String orderStatus;

   OrderDetailsClass(int id, String address, String grandTotal, String orderStatus) {
        this.id = id;
        this.address = address;
        this.grandTotal = grandTotal;
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
