package com.duc.chatting.home.views;

import static androidx.core.content.ContextCompat.getMainExecutor;

import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
                handler.removeCallbacks(suggestionRunnable);
                stopAutoScrollAndHideSuggestions(); // <<// Hủy gợi ý nếu user gửi tin nhắn
                addToChat(question, MessageBot.SENT_BY_ME);
                messageEditText.setText("");
                getResponse(question);
            }
        });
        showSuggestions(view);
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
    private void showSuggestions(View view) {
        String[] suggestions = {
                "✈️ Solo travel benefits",
                "🎓 Best schools in Europe",
                "💬 Quotes for workout",
                "🤖 Interview tips"
        };

        LinearLayout suggestionContainer = view.findViewById(R.id.suggestion_container);
        HorizontalScrollView scrollView = view.findViewById(R.id.suggestion_scroll);
        EditText messageEditText = view.findViewById(R.id.message_edit_text);

        suggestionContainer.removeAllViews(); // tránh trùng

        for (String suggestion : suggestions) {
            TextView chip = new TextView(getContext());
            chip.setText(suggestion);
            chip.setBackgroundResource(R.drawable.suggestion_chip_bg);
            chip.setPadding(40, 20, 40, 20);
            chip.setTextColor(Color.WHITE);
            chip.setTextSize(14);
            chip.setTypeface(Typeface.DEFAULT_BOLD);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 8, 16, 8);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                messageEditText.setText(suggestion);
                messageEditText.setSelection(suggestion.length());
            });

            suggestionContainer.addView(chip);
        }

        // Bắt đầu scroll sau khi layout sẵn sàng
//        scrollView.post(() -> startAutoScroll(scrollView, suggestionContainer));
    }

    private Handler scrollHandler = new Handler();
    private Runnable scrollRunnable;
    private boolean isScrolling = true;
    private void stopAutoScrollAndHideSuggestions() {
        isScrolling = false;
        scrollHandler.removeCallbacks(scrollRunnable);

        HorizontalScrollView scrollView = requireView().findViewById(R.id.suggestion_scroll);
        scrollView.setVisibility(View.GONE);
    }
    private void startAutoScroll(HorizontalScrollView scrollView, LinearLayout chipContainer) {
        final int scrollStep = 40; // mỗi lần cuộn bao nhiêu pixel
        final int delay = 100; // độ trễ giữa mỗi lần

        scrollRunnable = new Runnable() {
            int currentX = 0;

            @Override
            public void run() {
                if (!isScrolling) return;

                int maxScrollX = chipContainer.getWidth() - scrollView.getWidth();
                if (currentX >= maxScrollX) {
                    currentX = 0; // quay về đầu
                } else {
                    currentX += scrollStep;
                }

                scrollView.smoothScrollTo(currentX, 0);
                scrollHandler.postDelayed(this, delay);
            }
        };

        scrollHandler.post(scrollRunnable);
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