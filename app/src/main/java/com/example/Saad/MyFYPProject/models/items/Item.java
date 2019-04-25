package com.example.Saad.MyFYPProject.models.items;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;



@Entity
public class Item  {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String price;
    private String image;
    private String description;
    private String created_at;
    private String votes;
    private String updated_at;
    private String category;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void AddQuantity()
    {
        count++;
    }

    public void MinusQuantity()
    {
        if(count <= 0)
        {
            return;
        }
        count--;
    }
    // Getter Methods


    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void addVotes()
    {
      int i =  Integer.parseInt(this.votes);
      i++;
      this.votes = i+"";
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCategory() {
        return category;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
