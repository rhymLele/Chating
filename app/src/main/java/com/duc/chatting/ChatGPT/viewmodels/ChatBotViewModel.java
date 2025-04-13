package com.duc.chatting.ChatGPT.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.duc.chatting.ChatGPT.models.MessageBot;

import java.util.ArrayList;
import java.util.List;

public class ChatBotViewModel extends ViewModel {
    public final List<MessageBot> messageList = new ArrayList<>();
}
