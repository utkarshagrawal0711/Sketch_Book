package com.example.sketchbook.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sketchbook.R;

import org.jetbrains.annotations.NotNull;

public class PaintingItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView name;
    private ImageView delete;

    public PaintingItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.paintingImage);
        name = itemView.findViewById(R.id.paintingName);
        delete = itemView.findViewById(R.id.delete);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public ImageView getDelete() {
        return delete;
    }

    public void setDelete(ImageView delete) {
        this.delete = delete;
    }
}
