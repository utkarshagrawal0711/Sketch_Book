package com.example.sketchbook;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sketchbook.Model.PaintingsAdapter;
import com.example.sketchbook.db.AppRepository;
import com.example.sketchbook.db.Drawing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private LinearLayout layout;
    private RecyclerView recyclerView;
    private PaintingsAdapter adapter;

    private final int REQUEST_DRAW_ACTIVITY = 1000;
    public static Drawing drawingBackActivity = new Drawing();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Custom Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        fab = (FloatingActionButton) findViewById(R.id.addFloatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PaintActivity.class);
                drawingBackActivity=null;
                startActivityForResult(intent,REQUEST_DRAW_ACTIVITY);
            }
        });

        layout = (LinearLayout) findViewById(R.id.defaultLayout);
        recyclerView = (RecyclerView) findViewById(R.id.allFiles);

        getDrawings();
    }

    private void getDrawings() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        recyclerView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);

        new LoadDrawings().execute();
    }

    private List<File> getAllFiles() {
        ArrayList<File> files = new ArrayList<>();

        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+
                File.separator+getString(R.string.app_name));
        File[] paintings = directory.listFiles();

        for (File file: paintings) {
            if (file.getName().endsWith(".png")) {
                files.add(file);
            }
        }
        return files;
    }

    private List<Drawing> getAllDrawings() {
        AppRepository repository = new AppRepository(this);
        return repository.getMyDrawings();
    }

    class LoadDrawings extends AsyncTask<Void,Void,Void> {
        AppRepository repository;
        List<Drawing> myDrawings;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            repository=new AppRepository(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            myDrawings = repository.getMyDrawings();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter = new PaintingsAdapter(MainActivity.this,myDrawings);
            adapter.setOnItemClickListener(new PaintingsAdapter.onItemClickListener() {
                @Override
                public void onItemClick(Drawing drawing) {
                    Intent intent = new Intent(MainActivity.this,ViewActivity.class);
                    ViewActivity.drawing = drawing;
                    drawingBackActivity=null;
                    startActivity(intent);
                }
            });

            if (myDrawings.size()==0) {
                recyclerView.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                adapter.setOnLastDrawingDeleteListener(new PaintingsAdapter.onLastDrawingDeleteListener() {
                    @Override
                    public void onLastDrawingDeleted() {
                        recyclerView.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }
                });
            }

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode==REQUEST_DRAW_ACTIVITY) {
            if (drawingBackActivity!=null) {
                Log.d("Name: ",drawingBackActivity.getName());
                adapter.addDrawing(drawingBackActivity);
                //recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}