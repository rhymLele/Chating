package com.duc.chatting.home.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.R;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemContainerRecentConservationBinding;
import com.duc.chatting.home.interfaces.ConservationListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentConservationAdapter extends RecyclerView.Adapter<RecentConservationAdapter.ConservationViewHolder> {
    private ConservationListener conservationListener;
    private List<ChatMessage> chatMessages;

    @SuppressLint("NotifyDataSetChanged")
    public RecentConservationAdapter( List<ChatMessage> chatMessages,ConservationListener conservationListener) {
        this.conservationListener = conservationListener;
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConservationViewHolder(ItemContainerRecentConservationBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConservationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ConservationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConservationBinding binding;

        public ConservationViewHolder(@NonNull ItemContainerRecentConservationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
        public void setData(ChatMessage chatMessage) {
            if(chatMessage.getConservationName()!=null){
                binding.imageProfile.setImageBitmap(getConservationImage(chatMessage.getConservationImage()));
            }else {
                binding.imageProfile.setImageResource(R.drawable.baseline_person_24v2);
                binding.imageProfile.setBackgroundResource(R.drawable.background_profile);
            }
            binding.textName.setText(chatMessage.getConservationName());
            String receiverMessage=chatMessage.getSenderName()+":"+chatMessage.getMessage();
            String senderMessage="You:"+chatMessage.getMessage();
            if(chatMessage.getSenderName()!=null){
                binding.textRecentMessage.setText(receiverMessage);
                binding.textDate.setText(" . " + getReadableDate(chatMessage.getDateObject()));
            }else {
                binding.textRecentMessage.setText(senderMessage);
                binding.textDate.setText(" . " + getReadableDate(chatMessage.getDateObject()));
            }
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id=chatMessage.getConservationID();
                    String name=chatMessage.getConservationName();
                    String image=chatMessage.getConservationImage();
                    User user=new User(id,name,image);
                    conservationListener.onConservationClicked(user);
                }
            });
        }
        public Bitmap getConservationImage(String image){
            if (image == null || image.isEmpty()) {
                return null; // Tránh lỗi nếu dữ liệu bị null
            }
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        public String getReadableDate(Date date){
            if (date == null) {
                return ""; // hoặc trả về giá trị mặc định khác
            }
            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
        }
    }
}
