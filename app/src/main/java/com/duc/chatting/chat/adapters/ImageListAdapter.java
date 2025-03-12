package com.duc.chatting.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.models.ImageClass;
import com.duc.chatting.databinding.ItemContainerImageListBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder> {
    public ImageListAdapter(List<ImageClass> listImage) {
        this.listImage = listImage;
    }

    private List<ImageClass> listImage;

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageListViewHolder(ItemContainerImageListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
        holder.setData(listImage.get(position));
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder {
        ItemContainerImageListBinding binding;

        public ImageListViewHolder(@NonNull ItemContainerImageListBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void setData(ImageClass imageClass) {
            if (imageClass.getStatusImage().equals("enable")) {
                Picasso.get().load(imageClass.getUrlImage()).into(binding.roundImageFile);
            }
        }

    }
}
