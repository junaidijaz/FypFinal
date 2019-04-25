package com.example.Saad.MyFYPProject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class ItemClass extends RealmObject{


    private int id;
    private String  name;
    private String price;
    private String description;
    private String image;
    private String category;

    private int Count;

    public ItemClass() {
        this.id = 0;
        this.name = "";
        this.price = "";
        this.image = "";
        this.Count=0;
    }

    ItemClass(int id, String name, String price,String description, String image,String category, int count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description= description;
        this.image = image;
        this.Count=count;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    void setCount(int count) {
        Count = count;
    }

    int getCount() {
        return Count;
    }

    void AddQuantity()
    {
        Count++;
    }

    void MinusQuantity()
    {
        Count--;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

}
