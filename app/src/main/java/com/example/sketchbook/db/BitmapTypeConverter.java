package com.example.sketchbook.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class BitmapTypeConverter {

    // convert from bitmap to byte array
    @TypeConverter
    public byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    @TypeConverter
    public Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image,0, image.length);
    }

}
