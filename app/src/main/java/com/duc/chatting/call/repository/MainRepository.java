package com.duc.chatting.call.repository;

import android.content.Context;
import android.util.Log;

import com.duc.chatting.call.interfaces.ErrorCallBack;
import com.duc.chatting.call.interfaces.NewEventCallBack;
import com.duc.chatting.call.interfaces.SuccessCallBack;
import com.duc.chatting.call.models.DataModel;
import com.duc.chatting.call.models.DataModelType;
import com.duc.chatting.call.remote.FirebaseClient;
import com.duc.chatting.call.webrtc.MyPeerConnectionObserver;
import com.duc.chatting.call.webrtc.WebRTCClient;
import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

public class MainRepository implements WebRTCClient.Listener {

    public Listener listener;
    private final Gson gson = new Gson();
    private final FirebaseClient firebaseClient;

    private WebRTCClient webRTCClient;

    private String currentUsername;

    private SurfaceViewRenderer remoteView;

    private String target;
    private void updateCurrentUsername(String username){
        this.currentUsername = username;
    }

    private MainRepository(){
        this.firebaseClient = new FirebaseClient();
    }

    private static MainRepository instance;
    public static MainRepository getInstance(){
        if (instance == null){
            instance = new MainRepository();
        }
        return instance;
    }

    public void login(String username, Context context, SuccessCallBack callBack){
        Log.d("MainRepository", "Attempting login with username: " + username);

        firebaseClient.login(username,()->{
            updateCurrentUsername(username);
            this.webRTCClient = new WebRTCClient(context,new MyPeerConnectionObserver(){
                @Override
                public void onAddStream(MediaStream mediaStream) {
                    super.onAddStream(mediaStream);
                    try{
                        mediaStream.videoTracks.get(0).addSink(remoteView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                    Log.d("TAG", "onConnectionChange: "+newState);
                    super.onConnectionChange(newState);
                    Log.d("TAG", "State CONNECTED - listener is " + (listener == null ? "null" : "not null"));

                    if (newState == PeerConnection.PeerConnectionState.CONNECTED && listener!=null){
                        Log.d("TAG", "State CONNECTED - listener is " + (listener == null ? "null" : "not null"));

                        listener.webrtcConnected();
                    }

                    if (newState == PeerConnection.PeerConnectionState.CLOSED ||
                            newState == PeerConnection.PeerConnectionState.DISCONNECTED ){
                        if (listener!=null){
                            listener.webrtcClosed();
                        }
                    }
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {
                    super.onIceCandidate(iceCandidate);
                    webRTCClient.sendIceCandidate(iceCandidate,target);
                }
            },username);
            webRTCClient.listener = this;
            callBack.onSuccess();
        });
    }

    public void initLocalView(SurfaceViewRenderer view){
        if (webRTCClient == null) {
            Log.e("WebRTC", "webRTCClient is null");
            return;
        }
        if (view == null) {
            Log.e("WebRTC", "surfaceViewRenderer is null");
            return;
        }
        webRTCClient.initLocalSurfaceView(view);
    }

    public void initRemoteView(SurfaceViewRenderer view){
        webRTCClient.initRemoteSurfaceView(view);
        this.remoteView = view;
    }

    public void startCall(String target){
        webRTCClient.call(target);
    }

    public void switchCamera() {
        webRTCClient.switchCamera();
    }

    public void toggleAudio(Boolean shouldBeMuted){
        webRTCClient.toggleAudio(shouldBeMuted);
    }
    public void toggleVideo(Boolean shouldBeMuted){
        webRTCClient.toggleVideo(shouldBeMuted);
    }
    public void sendCallRequest(String target, ErrorCallBack errorCallBack){
        firebaseClient.sendMessageToOtherUser(
                new DataModel(target,currentUsername,null, DataModelType.StartCall),errorCallBack
        );
    }

    public void endCall(){
        webRTCClient.closeConnection();
//        firebaseClient.sendMessageToOtherUser(
//                new DataModel(target,currentUsername,null, DataModelType.EndCall),errorCallBack
//        );
//        firebaseClient.end(currentUsername,()->{});
    }

    public void subscribeForLatestEvent(NewEventCallBack callBack){
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()){

                case Offer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(
                            SessionDescription.Type.OFFER,model.getData()
                    ));
                    webRTCClient.answer(model.getSender());
                    break;
                case Answer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(
                            SessionDescription.Type.ANSWER,model.getData()
                    ));
                    break;
                case IceCandidate:
                        try{
                            IceCandidate candidate = gson.fromJson(model.getData(),IceCandidate.class);
                            webRTCClient.addIceCandidate(candidate);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    break;
                case StartCall:
                    this.target = model.getSender();
                    callBack.onNewEventReceived(model);
                    break;
            }

        });
    }

    @Override
    public void onTransferDataToOtherPeer(DataModel model) {
        firebaseClient.sendMessageToOtherUser(model,()->{});
    }

    public interface Listener{
        void webrtcConnected();
        void webrtcClosed();
    }
}
