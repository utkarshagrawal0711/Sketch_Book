package com.example.sketchbook.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class AppRepository {
    private AppDatabase database;

    Context context;

    public AppRepository(Context context) {
        this.context=context;
        this.database=AppDatabase.getInstance(context);
    }

    public void insertDrawing(Drawing drawing) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database.drawingDao().insertDrawing(drawing);
            }
        });
    }

    public List<Drawing> getMyDrawings() {
        List<Drawing> drawings = database.drawingDao().getAllDrawings();
        return drawings;
    }

    public void deleteDrawing(Drawing drawing) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                database.drawingDao().deleteDrawing(drawing);
            }
        });
    }

    public Drawing getDrawing(String name) {
        return database.drawingDao().getDrawing(name);
    }

    public void updateDrawing(String name, Bitmap bitmap) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Drawing drawing = new Drawing();
                drawing = database.drawingDao().getDrawing(name);
                drawing.setBitmap(bitmap);
                database.drawingDao().updateDrawing(drawing);
            }
        });
    }
}
