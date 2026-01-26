package com.br.salesbuddy.network;

import com.br.salesbuddy.model.ReprocessingRequest;
import com.br.salesbuddy.model.ReprocessingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReprocessingApi {

    @POST("reprocessing")
    Call<ReprocessingResponse> reprocessSale(
            @Header("Authorization") String token,
            @Body ReprocessingRequest requestBody
    );
}