package com.ygaps.androidlogin.network;

import com.ygaps.androidlogin.model.LoginRequest;
import com.ygaps.androidlogin.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by tpl on 12/13/16.
 */

public interface UserService {
    @POST("/login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/logout.php")
    Call<Void> logout();
}
