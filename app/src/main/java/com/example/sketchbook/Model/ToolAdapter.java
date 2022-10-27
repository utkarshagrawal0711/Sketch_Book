package com.example.sketchbook.Model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sketchbook.R;
import com.example.sketchbook.ViewHolder.ToolsViewClick;
import com.example.sketchbook.ViewHolder.ToolsViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolsViewHolder> {
    private ToolListener listener;
    private List<ToolItem> tools;
    private int current = -1;

    private Context context;

    public ToolAdapter(List<ToolItem> tools, ToolListener listener,Context context) {
        this.tools = tools;
        this.listener = listener;
        this.context=context;
    }

    @NonNull
    @NotNull
    @Override
    public ToolsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.tool_item,parent,false);
        Activity activity = (Activity) context;
        ToolsViewHolder holder = new ToolsViewHolder(view,activity);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ToolsViewHolder holder, int position) {
        holder.getName().setText(tools.get(position).getToolName());
        holder.getIcon().setImageResource(tools.get(position).getToolNumber());

        holder.setToolsViewClick(new ToolsViewClick() {
            @Override
            public void onToolClickEvent(int position) {
                current = position;
                listener.onSelection(tools.get(position).getToolName());
                notifyDataSetChanged();
            }
        });

        if (current == position) {
            holder.getName().setTypeface(holder.getName().getTypeface(), Typeface.BOLD);
        }
        else {
            holder.getName().setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }
}
