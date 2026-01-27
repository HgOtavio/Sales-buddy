package com.br.salesbuddy.network.api;

import com.br.salesbuddy.BuildConfig;
import com.br.salesbuddy.model.request.LoginRequest;
import com.br.salesbuddy.model.response.LoginResponse;

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