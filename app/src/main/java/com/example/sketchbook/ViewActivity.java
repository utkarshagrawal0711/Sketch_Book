package com.example.sketchbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sketchbook.db.Drawing;

public class ViewActivity extends AppCompatActivity {
    private TextView text;
    private ImageView imageView;
    public static Drawing drawing=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        text = (TextView) findViewById(R.id.toolBarHeading);
        text.setText(drawing.getName());

        imageView = (ImageView) findViewById(R.id.viewPainting);
        imageView.setImageBitmap(drawing.getBitmap());
    }
}