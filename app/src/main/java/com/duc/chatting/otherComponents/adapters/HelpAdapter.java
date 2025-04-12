package com.duc.chatting.otherComponents.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.R;
import com.duc.chatting.otherComponents.models.HelpItem;

import java.util.ArrayList;
import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    public interface OnHelpClickListener {
        void onClick(HelpItem item);
    }

    private final List<HelpItem> originalList;
    private List<HelpItem> displayList;
    private final OnHelpClickListener listener;

    public HelpAdapter(List<HelpItem> helpList, OnHelpClickListener listener) {
        this.originalList = helpList;
        this.displayList = new ArrayList<>(helpList);
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpItem item = displayList.get(position);
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return  displayList.size();
    }
    public void filterByKeyword(String keyword) {
        List<HelpItem> filtered = new ArrayList<>();
        for (HelpItem item : originalList) {
            if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(item);
            }
        }
        this.displayList = filtered;
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        if (category.equalsIgnoreCase("General")) {
            // Show all if tab is General
            this.displayList = new ArrayList<>(originalList);
        } else {
            List<HelpItem> filtered = new ArrayList<>();
            for (HelpItem item : originalList) {
                if (item.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(item);
                }
            }
            this.displayList = filtered;
        }
        notifyDataSetChanged();
        listener.onClick(null); // notify activity for empty check
    }

}