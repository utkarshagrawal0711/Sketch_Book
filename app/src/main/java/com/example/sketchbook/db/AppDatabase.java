package com.example.sketchbook.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Drawing.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DrawingDao drawingDao();

    private static volatile AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context,
                    AppDatabase.class,"SketchBook Database")
                    .build();
    }
}
