package com.br.salesbuddy.network;

import com.br.salesbuddy.model.SaleRequest;
import com.br.salesbuddy.model.SaleResponse;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SalesApi {

    @POST(com.br.salesbuddy.BuildConfig.ENDPOINT_VENDAS)
    Call<SaleResponse> sendSale(
            @Header("Authorization") String authToken,
            @Body SaleRequest request
    );

    @POST(com.br.salesbuddy.BuildConfig.ENDPOINT_VENDAS_EMAIL)
    Call<Void> requestReceiptEmail(
            @Header("Authorization") String authToken,
            @Body Map<String, Object> requestBody
    );
}