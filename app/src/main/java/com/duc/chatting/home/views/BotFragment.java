package com.duc.chatting.home.views;

import static androidx.core.content.ContextCompat.getMainExecutor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duc.chatting.ChatGPT.adapters.MessageBotAdapter;
import com.duc.chatting.ChatGPT.models.MessageBot;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenerativeModel gm = new GenerativeModel(
                "tunedModels/bakerymodel-8cezk86qyps4", // Replace with your tuned model ID
                "AIzaSyBWDjAJEa3kRCPU8kVyxBTnTAmH4qVvg3Q" // Replace with your API key
        );
        model = GenerativeModelFutures.from(gm);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_btn);
        messageAdapter = new MessageBotAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString();
            addToChat(question,MessageBot.SENT_BY_ME);
            messageEditText.setText("");
            getResponse(question);
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
                requireActivity().runOnUiThread(() -> addToChat(resultText,MessageBot.SENT_BY_BOT));
            }

            @Override
            public void onFailure(Throwable t) {
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
}