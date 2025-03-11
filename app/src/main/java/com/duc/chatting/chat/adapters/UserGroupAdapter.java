package com.duc.chatting.chat.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.interfaces.UserListeners;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemContainerRecentConservationBinding;

import java.util.List;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {
    private List<User> userList;
    private UserListeners userListeners;

    @SuppressLint("NotifyDataSetChanged")
    public UserGroupAdapter(UserListeners userListeners, List<User> userList) {
        this.userListeners = userListeners;
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserGroupViewHolder(
                ItemContainerRecentConservationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupViewHolder holder, int position) {
        holder.setData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList!=null?userList.size():0;
    }

    public class UserGroupViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConservationBinding binding;

        public UserGroupViewHolder(@NonNull ItemContainerRecentConservationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void setData(User user) {
            if (user.getImgProfile() != null) {
                binding.imageProfile.setImageBitmap(getConservationImage(user.getImgProfile()));
            }
            if (!user.getId().equals(user.getIsAdd())) {
                String aDDByName = "Added by " + user.getNameAdd();
                binding.textRecentMessage.setText(aDDByName);
                binding.getRoot().setOnClickListener(v -> {
                    userListeners.onUserClicked(user);
                });
            } else if (user.getId().equals(user.getIsAdd())) {
                binding.textRecentMessage.setText("Host");
            }
        }

    }

    private Bitmap getConservationImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
