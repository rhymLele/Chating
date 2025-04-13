package com.duc.chatting.home.views;

import static androidx.core.content.ContextCompat.getMainExecutor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duc.chatting.ChatGPT.adapters.MessageBotAdapter;
import com.duc.chatting.ChatGPT.models.MessageBot;
import com.duc.chatting.ChatGPT.viewmodels.ChatBotViewModel;
import com.duc.chatting.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;


public class BotFragment extends Fragment {
    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    List<MessageBot> messageList;
    MessageBotAdapter messageAdapter;
    private GenerativeModelFutures model;
    private ChatBotViewModel botViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenerativeModel gm = new GenerativeModel(
                "tunedModels/bakerymodel-8cezk86qyps4", // Replace with your tuned model ID
                "AIzaSyBWDjAJEa3kRCPU8kVyxBTnTAmH4qVvg3Q" // Replace with your API key
        );
        model = GenerativeModelFutures.from(gm);

    }
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable suggestionRunnable = new Runnable() {
        @Override
        public void run() {
            if (messageList.isEmpty()) { // Chỉ hiển thị nếu chưa có tin nhắn nào
                addToChat("Hi my name is Gem! Tell me about your day!'", MessageBot.SENT_BY_BOT);
            }
        }
    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        messageList = new ArrayList<>();
        botViewModel = new ViewModelProvider(requireActivity()).get(ChatBotViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_btn);
        messageList = botViewModel.messageList;
        messageAdapter = new MessageBotAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        if (messageList.isEmpty()) {
            handler.postDelayed(suggestionRunnable, 10000);
        }
        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString();
            if (!question.trim().isEmpty()) {
                handler.removeCallbacks(suggestionRunnable); // Hủy gợi ý nếu user gửi tin nhắn
                addToChat(question, MessageBot.SENT_BY_ME);
                messageEditText.setText("");
                getResponse(question);
            }
        });

    }
    void addToChat(String message,String sentBy){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new MessageBot(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bot, container, false);
    }
    private void getResponse(String query) {
        // Create a Content object with the user's query
        addTypingIndicator();
        Content content = new Content.Builder()
                .addText(query)
                .build();

        // Pass the Content object to generateContent
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                removeTypingIndicator();
                requireActivity().runOnUiThread(() -> addToChat(resultText,MessageBot.SENT_BY_BOT));
            }

            @Override
            public void onFailure(Throwable t) {
                removeTypingIndicator();
                requireActivity().runOnUiThread(() -> addToChat("Error"+t,MessageBot.SENT_BY_BOT));
            }
        }, getMainExecutor(getContext()));
    }
    void addTypingIndicator() {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new MessageBot("Bot typing...", MessageBot.SENT_BY_BOT));
            messageAdapter.notifyDataSetChanged();
        });
    }
    private void removeTypingIndicator() {
        requireActivity().runOnUiThread(() -> {
            for (int i = messageList.size() - 1; i >= 0; i--) {
                if (messageList.get(i).getMessage().equals("Bot typing...")) {
                    messageList.remove(i);
                    break; // Chỉ xóa một lần
                }
            }
            messageAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(suggestionRunnable); //
    }
}