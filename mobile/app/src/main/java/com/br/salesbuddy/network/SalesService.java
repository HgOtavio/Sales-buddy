package com.br.salesbuddy.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.br.salesbuddy.model.SaleData;
import com.br.salesbuddy.model.SaleItemRequest;
import com.br.salesbuddy.model.SaleRequest;
import com.br.salesbuddy.model.SaleResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesService {

    public interface SalesCallback {
        void onSuccess(long idVenda);
        void onError(String message);
    }

    private SalesApi salesApi;

    public SalesService() {
        salesApi = RetrofitClient.getClient().create(SalesApi.class);
    }

    private String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("salesToken", null);
        return token != null ? "Bearer " + token : "";
    }


    private String parseErrorBody(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorJson);

                if (jsonObject.has("message")) {
                    return jsonObject.getString("message");
                }
                else if (jsonObject.has("error")) {
                    return jsonObject.getString("error");
                }
            }
        } catch (Exception e) {
        }
        return "Erro no servidor (Código: " + response.code() + ")";
    }

    public void sendSale(Context context, SaleData sale, SalesCallback callback) {
        SaleRequest request = new SaleRequest();
        request.userId = String.valueOf(sale.userId);
        request.clientName = sale.nome;
        request.clientCpf = sale.cpf;
        request.clientEmail = sale.email;
        request.saleValue = sale.valorVenda;
        request.receivedValue = sale.valorRecebido;

        List<SaleItemRequest> itemsList = new ArrayList<>();

        if (sale.item != null && !sale.item.isEmpty()) {
            Map<String, Integer> contagem = new HashMap<>();
            String[] itensBrutos = sale.item.split(",");

            int totalDeItens = 0;

            for (String s : itensBrutos) {
                String nomeLimpo = s.trim();
                if (!nomeLimpo.isEmpty()) {
                    contagem.put(nomeLimpo, contagem.getOrDefault(nomeLimpo, 0) + 1);
                    totalDeItens++;
                }
            }

            double precoUnitario = totalDeItens > 0 ? (sale.valorVenda / totalDeItens) : 0.0;

            for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
                itemsList.add(new SaleItemRequest(entry.getKey(), entry.getValue(), precoUnitario));
            }
        } else {
            itemsList.add(new SaleItemRequest("Venda Avulsa", 1, sale.valorVenda));
        }

        request.items = itemsList;
        request.saleItems = itemsList;

        salesApi.sendSale(getToken(context), request).enqueue(new Callback<SaleResponse>() {
            @Override
            public void onResponse(Call<SaleResponse> call, Response<SaleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getGeneratedId());
                } else {
                    callback.onError(parseErrorBody(response));
                }
            }

            @Override
            public void onFailure(Call<SaleResponse> call, Throwable t) {
                callback.onError("Erro de Conexão: " + t.getMessage());
            }
        });
    }

    public void requestReceiptEmail(Context context, Map<String, Object> saleData, SalesCallback callback) {
        salesApi.requestReceiptEmail(getToken(context), saleData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(0L);
                } else {
                    callback.onError(parseErrorBody(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erro de Conexão: " + t.getMessage());
            }
        });
    }
}