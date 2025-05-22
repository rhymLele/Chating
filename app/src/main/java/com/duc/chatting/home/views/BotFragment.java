package com.duc.chatting.home.views;

import static androidx.core.content.ContextCompat.getMainExecutor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.LinearInterpolator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


public class BotFragment extends Fragment {
    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton,mic_btn;
    List<MessageBot> messageList;
    MessageBotAdapter messageAdapter;
    private GenerativeModelFutures model;
    private ChatBotViewModel botViewModel;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    private TextToSpeech textToSpeech;

    private boolean isListening = false;
    private SpeechRecognizer speechRecognizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenerativeModel gm = new GenerativeModel(
                "tunedModels/bakerymodel-8cezk86qyps4", // Replace with your tuned model ID
                "AIzaSyBWDjAJEa3kRCPU8kVyxBTnTAmH4qVvg3Q" // Replace with your API key
        );
        model = GenerativeModelFutures.from(gm);
//        initSpeech();
    }
    void initSpeech(){

            textToSpeech = new TextToSpeech(getContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    // Set UtteranceProgressListener instead of OnUtteranceCompletedListener
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            // Called when utterance starts (optional)
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            // Called when utterance is complete
                            if(isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    if (isListening) {
                                        startListening(); // Restart listening after speech is complete
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String s) {
                            // Called if an error occurs during utterance
                            if(isAdded())
                            {                            requireActivity().runOnUiThread(() -> {
                                addToChat("Error in text-to-speech: " + s, MessageBot.SENT_BY_BOT);
                            });
                        }}

                        @Override
                        public void onError(String utteranceId, int errorCode) {
                            // Called if an error occurs during utterance
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    addToChat("Error in text-to-speech: " + getTtsErrorText(errorCode), MessageBot.SENT_BY_BOT);
                                });
                            }
                        }
                    });

                }            });
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    // Not used, but required by interface
                }

                @Override
                public void onBeginningOfSpeech() {
                    // Not used, but required by interface
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    // Not used, but required by interface
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    // Not used, but required by interface
                }

                @Override
                public void onEndOfSpeech() {
                    // Not used, but required by interface
                }

                @Override
                public void onError(int error) {
                    Toast.makeText(getContext(),
                            "Error in speech recognition: ",
                            Toast.LENGTH_SHORT).show();
                    if(isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            addToChat("Stopped listening. Press the button to start again.", MessageBot.SENT_BY_BOT);
                            if (isListening) {
                                startListening(); // Retry on error if still listening
                            }
                        });
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty() && isListening) {
                        String spokenText = matches.get(0);
                        addToChat(spokenText, MessageBot.SENT_BY_ME);
                        processCommandWithGemini(spokenText);
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    // Not used, but required by interface
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    // Not used, but required by interface
                }
            });

            // Request permissions
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE},
                    REQUEST_RECORD_AUDIO_PERMISSION);

    }

    private void processCommandWithGemini(String command) {
        Content content = new Content.Builder()
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String response = result.getText();
              requireActivity().runOnUiThread(() -> {
                    addToChat( response,MessageBot.SENT_BY_BOT);
                    speak(response);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    String errorMsg = "Error processing command: " + t.getMessage();
                    addToChat(errorMsg,MessageBot.SENT_BY_BOT);
                    speak(errorMsg);
                    t.printStackTrace();
                });
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void toggleListening() {
        if (isListening) {
            // Stop listening
            isListening = false;

            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
            }
            speak("Stopped listening. Press the button to start again.");
        } else {
            // Start listening
            isListening = true;
            startListening();
        }
    }
    private void speak(String text) {
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
    }
    private void startListening() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            speechRecognizer.startListening(intent);
        } else {
            addToChat("Audio permission not granted",MessageBot.SENT_BY_BOT);
            isListening = false;
        }
    }
    private String getTtsErrorText(int errorCode) {
        switch (errorCode) {
            case TextToSpeech.ERROR:
                return "General text-to-speech error";
            case TextToSpeech.ERROR_NETWORK:
                return "Network error in text-to-speech";
            case TextToSpeech.ERROR_NETWORK_TIMEOUT:
                return "Network timeout in text-to-speech";
            case TextToSpeech.ERROR_INVALID_REQUEST:
                return "Invalid request in text-to-speech";
            case TextToSpeech.ERROR_SERVICE:
                return "Text-to-speech service error";
            case TextToSpeech.ERROR_OUTPUT:
                return "Output error in text-to-speech";
            case TextToSpeech.ERROR_SYNTHESIS:
                return "Synthesis error in text-to-speech";
            default:
                return "Unknown text-to-speech error";
        }
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
        mic_btn=view.findViewById(R.id.mic_btn);
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
//        initSpeech();
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
        mic_btn.setOnClickListener(v -> {
            toggleListening();
        });
        showSuggestions(view);
    }
    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "Không nhận diện được lời nói. Vui lòng thử lại.";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Lỗi mạng.";
            case SpeechRecognizer.ERROR_AUDIO:
                return "Lỗi âm thanh.";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Lỗi client.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Không có quyền ghi âm.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Trình nhận dạng đang bận.";
            default:
                return "Lỗi không xác định.";
        }
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
                "🤖 Interview tips",
                "📚 Book recommendations 2025",
                "🍽️ Easy 15-min recipes",
                "💡 Daily motivation quotes",
                "💻 Learn coding with AI",
                "📈 How to invest safely",
                "🎵 Focus music playlist",
                "🧘 Breathe and meditate tips",
                "🌍 Eco-friendly lifestyle tips",
                "🏋️‍♀️ Home workout plan",
                "📝 Productivity hacks",
                "🎯 Goal setting strategies",
                "👨‍💼 Career development tips",
                "🧳 Travel checklist essentials",
                "💤 Sleep improvement habits",
                "🗣️ Public speaking tips",
                "📷 Instagram photo tips"
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
        scrollView.post(() -> startAutoScroll2(scrollView, suggestionContainer));
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
    private void startAutoScroll2(HorizontalScrollView scrollView, LinearLayout chipContainer) {
        int maxScrollX = chipContainer.getWidth() - scrollView.getWidth();

        ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollX", 0, maxScrollX);
        animator.setDuration(50000); // 5 giây để scroll toàn bộ chiều dài
        animator.setInterpolator(new android.view.animation.LinearInterpolator()); // cuộn đều
        animator.setRepeatCount(ValueAnimator.INFINITE); // lặp vô hạn
        animator.setRepeatMode(ValueAnimator.REVERSE); // cuộn lại từ đầu

        animator.start();
    }

    private void startAutoScroll(HorizontalScrollView scrollView, LinearLayout chipContainer) {
        final int scrollStep = 5;   // mượt hơn
        final int delay = 20;       // lặp lại thường xuyên hơn

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