package com.duc.chatting.messaging.service;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.duc.chatting.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
public class WebSocketService extends Service {

    private static final String TAG = "WebSocketService";
    private static final String SERVER_URL = "ws://your-server-ip:3000"; // Thay bằng IP thực

    private WebSocket webSocket;
    private OkHttpClient client;
    private String currentUserId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentUserId = intent.getStringExtra("user_id");
        connectWebSocket();
        return START_STICKY;
    }

    private void connectWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_URL).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket opened");

                try {
                    JSONObject registerJson = new JSONObject();
                    registerJson.put("type", "REGISTER");
                    registerJson.put("userID", currentUserId);
                    webSocket.send(registerJson.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received: " + text);
                try {
                    JSONObject data = new JSONObject(text);
                    if (Objects.equals(data.getString("type"), "NEW_MESSAGE")) {
                        JSONObject messageObj = data.getJSONObject("message");

                        String sender = messageObj.getString("senderName");
                        String content = messageObj.getString("message");

                        showNotification(sender, content);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "WS_CHANNEL")
                .setSmallIcon(R.drawable.comments)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) webSocket.cancel();
        if (client != null) client.dispatcher().executorService().shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
