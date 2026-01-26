package com.br.salesbuddy.network;

import com.br.salesbuddy.BuildConfig;
import com.br.salesbuddy.model.LoginRequest;
import com.br.salesbuddy.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {

    @POST(BuildConfig.ENDPOINT_AUTH_LOGIN)
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET(BuildConfig.ENDPOINT_AUTH_VERIFY)
    Call<Void> validateSession(@Header("Authorization") String token);
}