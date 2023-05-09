package com.research.postrequestwithownapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    @GET("users")
    Call<List<Users>> getUsers();

    @POST("users")
    Call<Users> createUsers(@Body Users users);
}
