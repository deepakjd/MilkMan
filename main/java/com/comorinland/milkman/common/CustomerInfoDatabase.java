package com.comorinland.milkman.common;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by deepak on 28/2/18.
 */

@Database(entities = { CustomerInfo.class }, version = 1)
public abstract class CustomerInfoDatabase extends RoomDatabase
{
    private static final String DB_NAME = "CustomerInfoDatabase.db";
    private static volatile CustomerInfoDatabase instance;

    public static synchronized CustomerInfoDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = create(context);
        }
        return instance;
    }

    private static CustomerInfoDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                CustomerInfoDatabase.class,
                DB_NAME).build();
    }

    public abstract CustomerInfoDao customerInfoDao();

}