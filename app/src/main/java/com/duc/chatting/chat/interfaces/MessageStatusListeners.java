package com.duc.chatting.chat.interfaces;

import com.duc.chatting.chat.models.ChatMessage;

public interface MessageStatusListeners {
    void onUserClickedMessageStatus(ChatMessage chatMessage);
}
