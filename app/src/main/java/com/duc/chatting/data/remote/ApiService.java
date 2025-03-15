package com.duc.chatting.data.remote;

import java.util.List;

import cr424ak.nguyenquochoang.project_le_sserafim.model.RoomRequest;
import cr424ak.nguyenquochoang.project_le_sserafim.model.UploadAudioResponse;
import cr424ak.nguyenquochoang.project_le_sserafim.model.UploadImageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @GET("/rooms")
    Call<List<String>> getRooms();

    @POST("/createRoom")
    Call<String> createRoom(@Body RoomRequest roomRequest);

    @Multipart
    @Headers("multipart:true")
    @POST("/uploadImage")
    Call<UploadImageResponse> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @Headers("multipart:true")
    @POST("/uploadAudio")
    Call<UploadAudioResponse> uploadAudio(@Part MultipartBody.Part audio);
}
