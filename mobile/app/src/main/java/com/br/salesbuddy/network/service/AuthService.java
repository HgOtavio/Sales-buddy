package com.br.salesbuddy.network.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.br.salesbuddy.network.api.AuthApi;
import com.br.salesbuddy.model.request.LoginRequest;
import com.br.salesbuddy.model.response.LoginResponse;
import com.br.salesbuddy.network.config.RetrofitClient;
import com.br.salesbuddy.utils.storage.SessionManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {

    public interface LoginCallback {
        void onSuccess(int userId, String userName, String token);
        void onError(String errorMessage);
    }

    private AuthApi authApi;

    public AuthService() {
        authApi = RetrofitClient.getClient().create(AuthApi.class);
    }

    public void login(Context context, String user, String password, LoginCallback callback) {
        LoginRequest request = new LoginRequest(user, password);

        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginData = response.body();
                    String token = loginData.token;

                    int userId = loginData.userId != null ? loginData.userId : (loginData.id != null ? loginData.id : -1);
                    String userName = "Usuário";

                    try {
                        String[] split = token.split("\\.");
                        if (split.length > 1) {
                            String body = getJsonFromJwt(split[1]);
                            JSONObject jsonBody = new JSONObject(body);

                            if (userId == -1) userId = jsonBody.optInt("id", -1);
                            userName = jsonBody.optString("name", "Usuário");

                            Log.d("JWT_DECODE", "ID extraído: " + userId + " / Nome: " + userName);
                        }
                    } catch (Exception e) {
                        Log.e("JWT_ERROR", "Erro ao decodificar token: " + e.getMessage());
                    }

                    SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putString("salesToken", token)
                            .putInt("savedUserId", userId)
                            .apply();

                    callback.onSuccess(userId, userName, token);

                } else {
                    String errorMsg = "Erro desconhecido";
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            if (jsonError.has("message")) errorMsg = jsonError.getString("message");
                            else if (jsonError.has("error")) errorMsg = jsonError.getString("error");
                        } else {
                            errorMsg = "Erro " + response.code();
                        }
                    } catch (Exception e) {
                        errorMsg = "Erro " + response.code();
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void validateSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("salesToken", null);

        if (token == null) return;

        authApi.validateSession("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.forceLogout(context, "Sessão inválida. Por favor, entre novamente.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AuthService", "Falha na validação do token: " + t.getMessage());
            }
        });
    }

    private String getJsonFromJwt(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}