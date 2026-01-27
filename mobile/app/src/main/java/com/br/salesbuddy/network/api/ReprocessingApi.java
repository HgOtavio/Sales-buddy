package com.br.salesbuddy.network.api;

import com.br.salesbuddy.BuildConfig;
import com.br.salesbuddy.model.request.ApproveRequest;
import com.br.salesbuddy.model.request.ReprocessingRequest;
import com.br.salesbuddy.model.response.ReprocessingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReprocessingApi {

    // Cria o reprocessamento (Salva o erro)
    @POST(BuildConfig.ENDPOINT_REPROCESS_CREATE)
    Call<ReprocessingResponse> createReprocessing(
            @Header("Authorization") String token,
            @Body ReprocessingRequest requestBody
    );

    // Lista os itens pendentes
    @POST(BuildConfig.ENDPOINT_REPROCESS_LIST)
    Call<List<ReprocessingResponse>> listReprocessing(
            @Header("Authorization") String token,
            @Body ReprocessingRequest requestBody
    );

    // Aprova o reprocessamento (Transforma em Venda)
    @POST(BuildConfig.ENDPOINT_REPROCESS_APPROVE)
    Call<ReprocessingResponse> approveReprocessing(
            @Header("Authorization") String token,
            @Body ApproveRequest body
    );
}