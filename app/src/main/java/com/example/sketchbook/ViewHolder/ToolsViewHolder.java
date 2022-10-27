package com.example.sketchbook.ViewHolder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sketchbook.R;

public class ToolsViewHolder extends RecyclerView.ViewHolder {

    private ImageView icon;
    private TextView name;
    private ToolsViewClick toolsViewClick;

    public ToolsViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView, Activity activity) {
        super(itemView);

        icon = itemView.findViewById(R.id.toolIcon);
        name = itemView.findViewById(R.id.toolName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolsViewClick.onToolClickEvent(getAdapterPosition());
            }
        });
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public ToolsViewClick getToolsViewClick() {
        return toolsViewClick;
    }

    public void setToolsViewClick(ToolsViewClick toolsViewClick) {
        this.toolsViewClick = toolsViewClick;
    }
}
