package com.duc.chatting.poliso.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.interfaces.UserListeners;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemBlockUserBinding;
import com.duc.chatting.databinding.ItemUserRequestBinding;
import com.duc.chatting.home.adapters.FriendRequestAdapter;
import com.duc.chatting.home.interfaces.AcceptRequest;
import com.duc.chatting.home.interfaces.RejectRequest;

import java.util.List;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.BlockUserViewHolder> {
    private List<User> users;
    private UserListeners userListeners;
    private RejectRequest rejectRequest;
    public BlockUserAdapter(List<User> users, UserListeners userListeners, RejectRequest rejectRequest) {
        this.users = users;
        this.userListeners = userListeners;
        this.rejectRequest=rejectRequest;
    }

    @NonNull
    @Override
    public BlockUserAdapter.BlockUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlockUserBinding itemUserBinding = ItemBlockUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new BlockUserAdapter.BlockUserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockUserAdapter.BlockUserViewHolder holder, int position) {
        holder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class BlockUserViewHolder extends RecyclerView.ViewHolder {
        ItemBlockUserBinding binding;

        public BlockUserViewHolder(@NonNull ItemBlockUserBinding itemView) {
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
