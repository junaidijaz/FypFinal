package com.example.Saad.MyFYPProject.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.location.Criteria;

import com.example.Saad.MyFYPProject.models.items.Item;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem (Item item);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Item entity);

    @Query ("SELECT * FROM item")
    List<Item> getAllItems();

    @Query ("SELECT * FROM item where id= :id")
   Item getItembyId(String id);

    @Query ("delete FROM item")
   void deleteAllItems();

}
