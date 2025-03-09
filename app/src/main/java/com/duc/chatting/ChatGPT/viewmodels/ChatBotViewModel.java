package com.duc.chatting.ChatGPT.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.ChatGPT.models.MessageBot;

import java.util.ArrayList;
import java.util.List;

public class ChatBotViewModel extends AndroidViewModel {
    private final MutableLiveData<List<MessageBot>> messageList;

    public MutableLiveData<List<MessageBot>> getMessageList() {
        return messageList;
    }
    public ChatBotViewModel(@NonNull Application application) {
        super(application);
        messageList = new MutableLiveData<>();
    }
    public void addMessage(MessageBot message) {
        List<MessageBot> currentList = messageList.getValue();
        if (currentList != null) {
            currentList.add(message);
            messageList.postValue(currentList);
        }
    }
}
