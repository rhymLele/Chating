package com.duc.chatting.chat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.chat.interfaces.MessageStatusListeners;
import com.duc.chatting.chat.interfaces.PDFListeners;
import com.duc.chatting.chat.interfaces.UserListeners;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.databinding.ItemContainerReceiverMessageBinding;
import com.duc.chatting.databinding.ItemContainerSentMessageBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ChatMessage> chatMessages;
    private Bitmap receiverProfileImage;
    private String senderID;

    private PDFListeners pdfListeners;
    private UserListeners userListeners;
    private MessageStatusListeners messageStatusListeners;
    private String name="";
    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;

    public ChatAdapter(List<ChatMessage> chatMessages,String name, Bitmap receiverProfileImage, String senderID, PDFListeners pdfListeners,
                       UserListeners userListeners, MessageStatusListeners messageStatusListeners ) {
        notifyDataSetChanged();
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderID = senderID;
        this.pdfListeners = pdfListeners;
        this.userListeners = userListeners;
        this.messageStatusListeners = messageStatusListeners;
        this.name = name;
    }
    public ChatAdapter(List<ChatMessage> chatMessages,String name, String senderID, PDFListeners pdfListeners,
                       UserListeners userListeners, MessageStatusListeners messageStatusListeners ) {
        notifyDataSetChanged();
        this.chatMessages = chatMessages;
        this.senderID = senderID;
        this.pdfListeners = pdfListeners;
        this.userListeners = userListeners;
        this.messageStatusListeners = messageStatusListeners;
        this.name = name;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ReceiverMessageViewHolder(ItemContainerReceiverMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }else{
            ((ReceiverMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getSenderID().equals(senderID)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerSentMessageBinding binding;
        public SentMessageViewHolder(@NonNull ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            this.binding=itemContainerSentMessageBinding;
        }
        public void setData(ChatMessage chatMessage){
            if(chatMessage.getStatusMessage()!=null&&chatMessage.getStatusMessage().equals("disableForAll")){
                binding.textMessage.setText("Valid message");
            }else{
                if(chatMessage.getFileName()!=null){
                    binding.textMessage.setText(chatMessage.getFileName());
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));
                    binding.getRoot().setOnClickListener(v -> {
                        String nameFile=chatMessage.getFileName();
                        String urlFile=chatMessage.getUrlFile();
                        PDFClass pdfClass=new PDFClass(nameFile,urlFile);
                        pdfListeners.onUserClickedFileMessage(pdfClass);
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        }else if(binding.textDateTime.getVisibility()==View.VISIBLE){
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });

                    binding.getRoot().setOnLongClickListener(v -> {
                        messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                        return false;
                    });
                }
                //message image
                else if(chatMessage.getUrlImage()!=null){
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));
                    Picasso.get().load(chatMessage.getUrlImage()).into(binding.roundImageViewItemSent);
                    binding.roundImageViewItemSent.setVisibility(View.VISIBLE);
                    binding.textMessage.setVisibility(View.GONE);
                    binding.getRoot().setOnClickListener(v -> {
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        }else if(binding.textDateTime.getVisibility()==View.VISIBLE){
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });
                    binding.getRoot().setOnLongClickListener(v -> {
                        messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                        return false;
                    });

                }
                //message Text
                else{
                    binding.textMessage.setText(chatMessage.getMessage());
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));

                    //rep ib local
                    if(chatMessage.getMessageRepLocal()!=null){
                        binding.textMessageRepLocal.setVisibility(View.VISIBLE);
                        binding.textMessageRepLocal.setText(chatMessage.getMessageRepLocal());

                    }else if(chatMessage.getUrlImageRepLocal()!=null){
                        binding.roundImageViewRepLocal.setVisibility(View.VISIBLE);
                        Picasso.get().load(chatMessage.getUrlImageRepLocal()).into(binding.roundImageViewRepLocal);

                    }
                    binding.getRoot().setOnClickListener(v -> {
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        }else if(binding.textDateTime.getVisibility()==View.VISIBLE){
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });
                    binding.getRoot().setOnLongClickListener(v -> {
                        messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                        return false;
                    });
                }
            }
        }
        private String getReadableDateTime(Date date){

            return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
        }

    }
    //using xml receiver message(user receiver)
    public class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{
        ItemContainerReceiverMessageBinding binding;

        public ReceiverMessageViewHolder(@NonNull ItemContainerReceiverMessageBinding itemContainerReceiverMessageBinding) {
            super(itemContainerReceiverMessageBinding.getRoot());
            this.binding=itemContainerReceiverMessageBinding;
        }
        public void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            if(chatMessage.getStatusMessage()!=null&&chatMessage.getStatusMessage().equals("disableForAll")){
                String text="";

                if(chatMessage.getSenderName()!=null){
                    text=chatMessage.getSenderName() + ": Valid message";
                    if(receiverProfileImage!=null){
                        binding.imageProfile.setImageBitmap(receiverProfileImage);
                    }else if(chatMessage.getSenderImage()!=null){
                        binding.imageProfile.setImageBitmap(getBitmapFromEncodeString(chatMessage.getSenderImage()));
                    }
                    if(chatMessage.getSenderName()!=null){
                        binding.textNameSender.setVisibility(View.VISIBLE);
                        binding.textNameSender.setText(chatMessage.getSenderName());
                    }
                }else {
                    text=name + ": Valid message";
                    if(receiverProfileImage!=null){
                        binding.imageProfile.setImageBitmap(receiverProfileImage);
                    }else if(chatMessage.getSenderImage()!=null){
                        binding.imageProfile.setImageBitmap(getBitmapFromEncodeString(chatMessage.getSenderImage()));
                    }
                    if(chatMessage.getSenderName()!=null){
                        binding.textNameSender.setVisibility(View.VISIBLE);
                        binding.textNameSender.setText(chatMessage.getSenderName());
                    }

                }
                binding.textMessage.setText(text);
            }else{
                if(chatMessage.getFileName()!=null){
                    binding.textMessage.setText(chatMessage.getFileName());
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));
                    if(receiverProfileImage!=null){
                        binding.imageProfile.setImageBitmap(receiverProfileImage);
                    }else if(chatMessage.getSenderImage()!=null){
                        binding.imageProfile.setImageBitmap(getBitmapFromEncodeString(chatMessage.getSenderImage()));
                    }
                    if(chatMessage.getSenderName()!=null){
                        binding.textNameSender.setVisibility(View.VISIBLE);
                        binding.textNameSender.setText(chatMessage.getSenderName());
                    }
                    binding.getRoot().setOnClickListener(v -> {
                        String nameFile=chatMessage.getFileName();
                        String urlFile=chatMessage.getUrlFile();
                        PDFClass pdfClass=new PDFClass(nameFile,urlFile);
                        pdfListeners.onUserClickedFileMessage(pdfClass);
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        }else if(binding.textDateTime.getVisibility()==View.VISIBLE){
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });
                    binding.getRoot().setOnLongClickListener( v -> {
                        messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                        return false;
                    });
                }
                //set image display chat
                else if(chatMessage.getUrlImage()!=null){
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));
                    if(receiverProfileImage!=null){
                        binding.imageProfile.setImageBitmap(receiverProfileImage);
                    }else if(chatMessage.getSenderImage()!=null){
                        binding.imageProfile.setImageBitmap(getBitmapFromEncodeString(chatMessage.getSenderImage()));
                    }
                    if(chatMessage.getSenderName()!=null){
                        binding.textNameSender.setVisibility(View.VISIBLE);
                        binding.textNameSender.setText(chatMessage.getSenderName());
                    }
                    Picasso.get().load(chatMessage.getUrlImage()).into(binding.roundedImageViewItemReceiver);
                    binding.roundedImageViewItemReceiver.setVisibility(View.VISIBLE);
                    binding.textMessage.setVisibility(View.GONE);
                    binding.getRoot().setOnClickListener(v -> {
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        }else if(binding.textDateTime.getVisibility()==View.VISIBLE){
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });
                    binding.getRoot().setOnLongClickListener(v -> {
                        messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                        return false;
                    });
                }
                else{
                    binding.textMessage.setText(chatMessage.getMessage());
                    binding.textDateTime.setText(getReadableDateTime(chatMessage.getDateTime()));
                    if(receiverProfileImage!=null){
                        binding.imageProfile.setImageBitmap(receiverProfileImage);
                    } else if (chatMessage.getSenderImage()!=null)
                    {
                        binding.imageProfile.setImageBitmap(getBitmapFromEncodeString(chatMessage.getSenderImage()));
                        
                    }
                    if(chatMessage.getSenderName()!=null){
                        binding.textNameSender.setVisibility(View.VISIBLE);
                        binding.textNameSender.setText(chatMessage.getSenderName());

                    }
                    //rep ib local
                    if(chatMessage.getMessageRepLocal()!=null){
                        binding.textMessageRepLocal.setVisibility(View.VISIBLE);
                        binding.textMessageRepLocal.setText(chatMessage.getMessageRepLocal());
                    }else if(chatMessage.getUrlImageRepLocal()!=null){
                        binding.roundImageViewRepLocal.setVisibility(View.VISIBLE);
                        Picasso.get().load(chatMessage.getUrlImageRepLocal()).into(binding.roundImageViewRepLocal);

                    }
                    binding.getRoot().setOnClickListener(v -> {
                        if(binding.textDateTime.getVisibility()==View.GONE){
                            binding.textDateTime.setVisibility(View.VISIBLE);
                        } else if (binding.textDateTime.getVisibility()==View.VISIBLE) {
                            binding.textDateTime.setVisibility(View.GONE);
                        }
                    });
                }
            }
            //click to profile user
            binding.imageProfile.setOnClickListener(v -> {
                String userID=chatMessage.getSenderID();
                User user=new User(userID);
                userListeners.onUserClicked(user);
            });
            binding.getRoot().setOnLongClickListener(v -> {
                messageStatusListeners.onUserClickedMessageStatus(chatMessage);
                return false;
            });
        }
        private String getReadableDateTime(Date date){

            return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
        }
        private Bitmap getBitmapFromEncodeString(String encodeImage){
            byte[] bytes= Base64.decode(encodeImage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        }
    }
}
