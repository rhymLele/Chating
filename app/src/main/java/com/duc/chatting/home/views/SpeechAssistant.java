package com.duc.chatting.home.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;

public class SpeechAssistant {
    private final Context context;
    private  TextToSpeech tts;
    private final SpeechRecognizer recognizer;
    private boolean isListening = false;

    private final GenerativeModelFutures generativeModel;

    public interface Callback {
        void onResult(String userText, String aiResponse);
        void onError(String errorMsg);
    }

    public SpeechAssistant(Context context) {
        this.context = context;
        this.tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        recognizer = SpeechRecognizer.createSpeechRecognizer(context);

        // Gemini setup
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                "AIzaSyBWDjAJEa3kRCPU8kVyxBTnTAmH4qVvg3Q"
        );
        generativeModel = GenerativeModelFutures.from(gm);
    }

    public void startListening(Callback callback) {
        isListening = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String userText = matches.get(0);
                    processWithGemini(userText, callback);
                }
            }
            @Override public void onError(int error) {
                callback.onError("Speech recognition error: " + error);
            }

            // Other methods: onReadyForSpeech(), etc. can be empty
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        recognizer.startListening(intent);
    }

    private void processWithGemini(String userText, Callback callback) {
        Content content = new Content.Builder()
                .addText(userText)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiResponse = result.getText();
                speak(aiResponse);
                callback.onResult(userText, aiResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError("Gemini error: " + t.getMessage());
            }
        }, Executors.newSingleThreadExecutor());
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
    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ttsID");
    }

    public void stop() {
        if (isListening) {
            recognizer.stopListening();
            isListening = false;
        }
        tts.shutdown();
        recognizer.destroy();
    }
}
