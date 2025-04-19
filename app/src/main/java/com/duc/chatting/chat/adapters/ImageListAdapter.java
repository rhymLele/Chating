package com.duc.chatting.chat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
                binding.roundImageFile.setImageBitmap(getBitmapFromEncodeString(imageClass.getUrlImage()));
//                Picasso.get().load(imageClass.getUrlImage()).into(binding.roundImageFile);
            }
        }

    }
    private Bitmap getBitmapFromEncodeString(String encodeImage){
        byte[] bytes= Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
