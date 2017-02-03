package com.ygaps.androidlogin.network;

import com.ygaps.androidlogin.model.LoginRequest;
import com.ygaps.androidlogin.model.LoginResponse;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by tpl on 12/13/16.
 */

public interface UserService {
    @POST("/login.php")
    void login(@Body LoginRequest request, Callback<LoginResponse> callback);

    @POST("/logout.php")
    void logout(Callback<Response> callback);
}
