package com.duc.chatting.home.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.adapters.UserAdapter;
import com.duc.chatting.chat.interfaces.UserListeners;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemUserBinding;
import com.duc.chatting.databinding.ItemUserRequestBinding;
import com.duc.chatting.home.interfaces.AcceptRequest;
import com.duc.chatting.home.interfaces.RejectRequest;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {
    private List<User> users;
    private UserListeners userListeners;

    private AcceptRequest acceptRequest;
    private RejectRequest rejectRequest;
    public FriendRequestAdapter(List<User> users, UserListeners userListeners, AcceptRequest acceptRequest, RejectRequest rejectRequest) {
        this.users = users;
        this.userListeners = userListeners;
        this.acceptRequest=acceptRequest;
        this.rejectRequest=rejectRequest;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserRequestBinding itemUserBinding = ItemUserRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FriendRequestViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.FriendRequestViewHolder holder, int position) {
        holder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        ItemUserRequestBinding binding;

        public FriendRequestViewHolder(@NonNull ItemUserRequestBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void setData(User user) {
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail());
            if (user.getImgProfile() != null) {
                binding.imageProfile.setImageBitmap(getUserBitmap(user.getImgProfile()));
            }
            binding.getRoot().setOnClickListener(v -> {
                userListeners.onUserClicked(user);
            });
            binding.btnAccept.setOnClickListener(v -> {
                acceptRequest.onRequestAccept(user);
            });
            binding.btnReject.setOnClickListener(v -> {
                rejectRequest.onRequestReject(user);
            });
        }
    }

    private Bitmap getUserBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}