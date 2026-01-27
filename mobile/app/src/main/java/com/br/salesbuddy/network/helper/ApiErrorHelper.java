package com.br.salesbuddy.network.helper;

import org.json.JSONObject;
import retrofit2.Response;

public class ApiErrorHelper {
    public static String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorJson);
                if (jsonObject.has("message")) return jsonObject.getString("message");
                if (jsonObject.has("error")) return jsonObject.getString("error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro no servidor (" + response.code() + ")";
    }
}