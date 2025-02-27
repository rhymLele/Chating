package com.duc.chatting.chat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.lang.UScript;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.interfaces.UserListeners;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemContainerGroupChatBinding;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {
    private List<User> users;
    private UserListeners userListeners;
    public GroupChatAdapter(List<User> users,UserListeners userListeners){
        this.users=users;
        this.userListeners=userListeners;
    }
    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerGroupChatBinding itemContainerGroupChatBinding=ItemContainerGroupChatBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GroupChatViewHolder(itemContainerGroupChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position) {
        holder.setData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class GroupChatViewHolder extends RecyclerView.ViewHolder{
       ItemContainerGroupChatBinding binding;
        public GroupChatViewHolder(@NonNull ItemContainerGroupChatBinding binding ){
            super(binding.getRoot());
            this.binding=binding;
        }
        public void setData(User user){
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail());
            if(user.getImgProfile()!=null){
                binding.imageProfile.setImageBitmap(getUserImage(user.getImgProfile()));

            }
            binding.getRoot().setOnClickListener(v -> {
                if(binding.radioButton.isChecked()==true){
                    binding.radioButton.setChecked(false);
                    user.setChecked(false);
                    userListeners.onUserClicked(user);
                }else{
                    binding.radioButton.setChecked(true);
                    user.setChecked(true);
                    userListeners.onUserClicked(user);
                }
            });
        }
        private Bitmap getUserImage(String encodedImage){
            byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
    }
}
