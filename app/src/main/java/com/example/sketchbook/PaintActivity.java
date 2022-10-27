package com.example.sketchbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sketchbook.Model.PaintView;
import com.example.sketchbook.Model.ToolAdapter;
import com.example.sketchbook.Model.ToolItem;
import com.example.sketchbook.Model.ToolListener;
import com.example.sketchbook.Model.ToolName;
import com.example.sketchbook.db.AppRepository;
import com.example.sketchbook.db.Drawing;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity {
    PaintView paintView;
    int backgroundColor,brushColor;
    int brushSize,eraserSize;
    private ImageView download;
    private ImageView save;
    private ImageView share;
    private ImageView back;
    private final int REQUEST_PERMISSION_DOWNLOAD = 1001;
    private final int REQUEST_PERMISSION_READ = 1002;
    private static final int PICK_IMAGE = 1000;

    //public static Drawing drawing = null;
    public static boolean savedStatus = true; // Initially nothing drawn - need not to save
    private boolean onceSaved = false;
    private Drawing savedDrawing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        download = (ImageView) findViewById(R.id.download);
        save = (ImageView) findViewById(R.id.save);
        share = (ImageView) findViewById(R.id.share);
        back = (ImageView) findViewById(R.id.back);

        initialiseTools();

        // pass the height and width of the custom view
        // to the init method of the DrawView object
        ViewTreeObserver viewTreeObserver = paintView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paintView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paintView.getMeasuredWidth();
                int height = paintView.getMeasuredHeight();
                paintView.init(width,height);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // Check for permissions first
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                        PackageManager.PERMISSION_DENIED) {

                    // Request for permissions
                    requestPermissions(new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_DOWNLOAD);
                }
                // If permission granted
                else {
                    downloadDrawing();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                saveDrawing();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDrawing();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialiseTools() {
        backgroundColor=Color.WHITE;
        brushColor=Color.BLACK;
        paintView=findViewById(R.id.paintTab);
        brushSize=10;
        eraserSize=10;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.toolsTab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        ToolAdapter adapter = new ToolAdapter(getTools(), new ToolListener() {
            @Override
            public void onSelection(String name) {
                ToolActionSpecific(name);
            }
        },this);
        recyclerView.setAdapter(adapter);
    }

    private List<ToolItem> getTools() {
        List<ToolItem> tools = new ArrayList<>();

        tools.add(new ToolItem(R.drawable.brush, ToolName.BRUSH));
        tools.add(new ToolItem(R.drawable.palette,ToolName.COLOR));
        tools.add(new ToolItem(R.drawable.eraser,ToolName.ERASER));
        tools.add(new ToolItem(R.drawable.background,ToolName.BACKGROUND));
        tools.add(new ToolItem(R.drawable.image,ToolName.IMAGE));
        tools.add(new ToolItem(R.drawable.undo,ToolName.UNDO));
        tools.add(new ToolItem(R.drawable.redo,ToolName.REDO));

        return tools;
    }

    private void ToolActionSpecific(String name) {
        switch (name) {
            case ToolName.BRUSH:
                showSizeDialog(1);
                break;

            case ToolName.ERASER:
                showSizeDialog(2);
                break;

            case ToolName.BACKGROUND:
                updateBackgroundColor();
                break;

            case ToolName.COLOR:
                updateBrushColor();
                break;

            case ToolName.UNDO:
                paintView.undo();
                break;

            case ToolName.REDO:
                paintView.redo();
                break;

            case ToolName.IMAGE:
                addImage();
                break;
        }
    }

    /*
    Status code is 1 for brush and 2 for eraser
    */
    private void showSizeDialog(int statusCode) {
        int prevBrushSize=brushSize;
        int prevEraserSize=eraserSize;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.tool_dialog,null,false);

        TextView toolSelected = view.findViewById(R.id.toolSelected);
        TextView toolSize = view.findViewById(R.id.toolSize);
        ImageView toolImage = view.findViewById(R.id.toolImage);
        SeekBar seekBar = view.findViewById(R.id.sizeSeekBar);
        Button ok = view.findViewById(R.id.ok);
        Button cancel = view.findViewById(R.id.cancel);
        seekBar.setMax(99);
        seekBar.setPadding(0,0,0,0);

        if (statusCode==1) {
            toolSelected.setText("Brush Size");
            seekBar.setProgress(brushSize);
            toolImage.setImageResource(R.drawable.brush);
            paintView.setBrushSize(brushSize);
            toolSize.setText("Current size is " + brushSize);
        }
        // Status code == 2
        else  {
            toolSelected.setText("Eraser Size");
            seekBar.setProgress(eraserSize);
            toolImage.setImageResource(R.drawable.eraser);
            paintView.setEraserSize(eraserSize);
            toolSize.setText("Current size is " + eraserSize);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (statusCode==1) {
                    brushSize=progress+1;
                    toolSize.setText("Current size is " + brushSize);
                    paintView.setBrushSize(brushSize);
                }
                else {
                    eraserSize=progress+1;
                    toolSize.setText("Current size is " + eraserSize);
                    paintView.setEraserSize(eraserSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (statusCode==1) {
                    brushSize=prevBrushSize;
                    paintView.setBrushSize(prevBrushSize);
                }
                else {
                    eraserSize=prevEraserSize;
                    paintView.setEraserSize(prevEraserSize);
                }
            }
        });

        builder.setView(view);
        AlertDialog alertDialog=builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (statusCode==1) {
                    brushSize=prevBrushSize;
                    paintView.setBrushSize(prevBrushSize);
                }
                else {
                    eraserSize=prevEraserSize;
                    paintView.setEraserSize(prevEraserSize);
                }
            }
        });

        alertDialog.show();
    }

    private void updateBackgroundColor() {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .create();
        ColorPickerDialogListener listener = new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                backgroundColor = color;
                paintView.setBackgroundColor(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        };
        colorPickerDialog.setColorPickerDialogListener(listener);
        colorPickerDialog.show(getSupportFragmentManager(),"COLOR_PICKER");
    }

    private void updateBrushColor() {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .create();
        ColorPickerDialogListener listener = new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                brushColor = color;
                paintView.setBrushColor(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        };
        colorPickerDialog.setColorPickerDialogListener(listener);
        colorPickerDialog.show(getSupportFragmentManager(),"COLOR_PICKER");
    }

    private void downloadDrawing() {
        Bitmap bitmap = paintView.getBitmap();

        // Create unique file name
        String filename = UUID.randomUUID().toString();

        MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,filename,"Sketch Book");
        Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show();
    }

    private void saveDrawing() {
        if (onceSaved) {
            AppRepository repository = new AppRepository(this);
            repository.updateDrawing(savedDrawing.getName(),paintView.getBitmap());
            MainActivity.drawingBackActivity=new Drawing(savedDrawing.getName(),paintView.getBitmap());
            Toast.makeText(PaintActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            savedStatus=true;
            return;
        }

        Bitmap bitmap = paintView.getBitmap();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.save_dialog,null,false);

        EditText saveAs = view.findViewById(R.id.saveAs);
        Button ok = view.findViewById(R.id.ok_save);
        Button cancel = view.findViewById(R.id.cancel_save);

        builder.setView(view);
        AlertDialog alertDialog=builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String name=saveAs.getText().toString();
                AppRepository repository = new AppRepository(PaintActivity.this);
                if (name.length()==0) {
                    Toast.makeText(PaintActivity.this, "Name Cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    new CheckDrawing(name,bitmap).execute();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_DOWNLOAD &&
                grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            downloadDrawing();
        }
        else if (requestCode == REQUEST_PERMISSION_READ) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                addImage();
            }
            else {
                Toast.makeText(this, "Cannot add image", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    class CheckDrawing extends AsyncTask<Void,Void,Void> {
        AppRepository repository;
        String name;
        Drawing temp;
        Bitmap bitmap;

        public CheckDrawing(String name,Bitmap bitmap) {
            this.name = name;
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            repository=new AppRepository(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            temp=repository.getDrawing(name);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (temp!=null) {
                Toast.makeText(PaintActivity.this, "Not saved - Name already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                Drawing drawing = new Drawing();
                drawing.setName(name);
                drawing.setBitmap(bitmap);
                repository.insertDrawing(drawing);
                Toast.makeText(PaintActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                savedDrawing=drawing;
                MainActivity.drawingBackActivity=savedDrawing;
                savedStatus=true;
                onceSaved=true;
            }
        }
    }

    private void shareDrawing() {
        saveDrawingToInternalStorage();

        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.sketchbook", newFile);

        if (contentUri!=null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    private void saveDrawingToInternalStorage() {
        Bitmap bitmap = paintView.getBitmap();

        // save bitmap to cache directory
        try {

            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addImage() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_READ);
                return;
            }
        } else {
            Toast.makeText(this, "Cannot add image", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data!=null && resultCode==RESULT_OK) {
                Uri pickedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage,filePath,null,null,null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
                if (bitmap==null) {
                    Toast.makeText(this, "Cannot add image", Toast.LENGTH_SHORT).show();
                }
                else {
                    paintView.setImage(bitmap);
                }

                cursor.close();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!savedStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaintActivity.this);
            builder.setTitle( Html.fromHtml("<font color='#505050'>Warning</font>") );
            if (onceSaved) {
                builder.setMessage( Html.fromHtml("<font color='#505050'>Your changes will not be saved. Are you sure?</font>") );
            } else {
                builder.setMessage( Html.fromHtml("<font color='#505050'>Your drawing will not be saved. Are you sure?</font>") );
            }
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (savedDrawing!=null) {
                        savedDrawing.setBitmap(paintView.getBitmap());
                        MainActivity.drawingBackActivity = savedDrawing;
                    }
                    finish();
                    PaintActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.color.myWhite);
            dialog.show();
        }
        else {
            if (savedDrawing!=null) {
                savedDrawing.setBitmap(paintView.getBitmap());
                MainActivity.drawingBackActivity = savedDrawing;
            }
            super.onBackPressed();
        }
    }
}