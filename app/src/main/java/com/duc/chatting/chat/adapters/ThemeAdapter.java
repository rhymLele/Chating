package com.duc.chatting.chat.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.duc.chatting.R;
import com.duc.chatting.chat.interfaces.OnThemeClick;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.ThemeItem;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {
    private List<ThemeItem> loveList;
    private Context context;
    OnThemeClick onThemeClick;

    public ThemeAdapter(Context context, List<ThemeItem> loveList, OnThemeClick onThemeClick) {
        this.context = context;
        this.onThemeClick = onThemeClick;
        this.loveList = loveList;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false);
        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        ThemeItem item = loveList.get(position);
        holder.textLove.setText(item.getName());
        try {
            holder.textLove.setBackgroundResource((Integer) item.getImageUrl());
        } catch (Exception e) {
            holder.textLove.setBackground(new ColorDrawable((Integer) item.getImageUrl()));
        }
        holder.textLove.setOnClickListener(v ->
        {
            onThemeClick.onUserClickedTheme(item);
        });
    }

    @Override
    public int getItemCount() {
        return loveList.size();
    }

    public static class ThemeViewHolder extends RecyclerView.ViewHolder {
        TextView textLove;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            textLove = itemView.findViewById(R.id.text);
        }
    }
}

