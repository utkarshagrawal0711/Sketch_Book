package com.example.sketchbook.db;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

@Entity(tableName = "drawings")
public class Drawing implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "drawingID")
    int id;

    @ColumnInfo(name = "drawingName")
    String name;

    @TypeConverters(BitmapTypeConverter.class)
    @ColumnInfo(name = "drawingBitmap")
    Bitmap bitmap;

    public Drawing() {
    }

    public Drawing(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
