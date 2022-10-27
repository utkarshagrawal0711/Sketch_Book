package com.example.sketchbook.Model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sketchbook.R;
import com.example.sketchbook.ViewHolder.PaintingItemViewHolder;
import com.example.sketchbook.db.AppRepository;
import com.example.sketchbook.db.Drawing;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaintingsAdapter extends RecyclerView.Adapter<PaintingItemViewHolder> {

    private Context context;
    private List<Drawing> drawingsList;

    // Click listener object created for last plan deletion
    private onLastDrawingDeleteListener listener;

    // Click listener object created for recycler view item click
    private onItemClickListener itemClickListener;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Drawing> getDrawingsList() {
        return drawingsList;
    }

    public void setDrawingsList(List<Drawing> drawingsList) {
        this.drawingsList = drawingsList;
    }

    public PaintingsAdapter(Context context, List<Drawing> drawingsList) {
        this.context = context;
        this.drawingsList = drawingsList;
    }

    public void setOnLastDrawingDeleteListener(onLastDrawingDeleteListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public PaintingItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.painting_item,parent,false);
        return new PaintingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PaintingItemViewHolder holder, int position) {
        holder.getImageView().setImageBitmap(drawingsList.get(position).getBitmap());
        holder.getName().setText(drawingsList.get(position).getName());
        holder.getName().setSelected(true);
        holder.getName().setMarqueeRepeatLimit(1);

        holder.getDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateDeleteDialog(position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(drawingsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawingsList.size();
    }

    // Interface to perform action on all plans deleted event
    public interface onLastDrawingDeleteListener {
        void onLastDrawingDeleted();
    }

    public interface onItemClickListener {
        void onItemClick(Drawing drawing);
    }

    @SuppressLint("ResourceAsColor")
    private void generateDeleteDialog(int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle( Html.fromHtml("<font color='#505050'>Warning</font>") );
        builder.setMessage( Html.fromHtml("<font color='#505050'>Are you sure you want to delete</font>") );
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppRepository repository = new AppRepository(context);
                Drawing drawing = drawingsList.get(position);
                drawingsList.remove(position);
                repository.deleteDrawing(drawing);
                if (drawingsList.size()==0 && listener!=null) {
                    listener.onLastDrawingDeleted();
                }
                notifyDataSetChanged();
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
        dialog.getWindow().setTitleColor(R.color.AppBarColor);
        dialog.show();
    }

    public void addDrawing(Drawing drawingToAdd) {
        drawingsList.add(0,drawingToAdd);
        notifyDataSetChanged();
    }
}
