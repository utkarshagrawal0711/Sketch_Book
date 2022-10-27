package com.example.sketchbook.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DrawingDao {
    @Query("SELECT * FROM drawings ORDER BY drawingID DESC")
    List<Drawing> getAllDrawings();

    @Insert
    void insertDrawing(Drawing drawing);

    @Delete
    void deleteDrawing(Drawing drawing);

    @Update
    void updateDrawing(Drawing drawing);

    @Query("SELECT * FROM drawings WHERE drawingName=:value")
    Drawing getDrawing(String value);
}
