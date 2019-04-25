package com.example.Saad.MyFYPProject.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.Saad.MyFYPProject.models.items.Item;

@Database(entities = {Item.class}, version = 1)
public abstract class DatabaseClient  extends RoomDatabase   {
    private static DatabaseClient INSTANCE;

    public abstract DaoAccess userDao();

    public static DatabaseClient getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), DatabaseClient.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
