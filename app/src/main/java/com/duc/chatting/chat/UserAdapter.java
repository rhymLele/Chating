package com.duc.chatting.chat;

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
import com.duc.chatting.databinding.ItemUserBinding;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private UserListeners userListeners;
    public UserAdapter(List<User> users,UserListeners userListeners) {
        this.users = users;
        this.userListeners=userListeners;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding=ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void setData(User user) {
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail());
            if (user.getImgProfile() != null) {
                binding.imageProfile.setImageBitmap(getUserBitmap(user.getImgProfile()));
            }
            binding.getRoot().setOnClickListener(v->{
                    userListeners.onUserClicked(user);
            });
        }
    }

    private Bitmap getUserBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
