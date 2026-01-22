package com.br.salesbuddy.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.br.salesbuddy.BuildConfig;
import com.br.salesbuddy.utils.SessionManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    public interface LoginCallback {
        void onSuccess(int userId, String userName, String token);
        void onError(String errorMessage);
    }

    public void login(Context context, String user, String password, LoginCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BuildConfig.BASE_URL + "/auth/login");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user", user);
                jsonParam.put("password", password);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    String responseText = readStream(conn.getInputStream());
                    JSONObject jsonResponse = new JSONObject(responseText);

                    String token = jsonResponse.optString("token", "");

                    int userId = -1;
                    String userName = "Usuário";

                    try {
                        String[] split = token.split("\\.");
                        if (split.length > 1) {
                            String body = getJson(split[1]);
                            JSONObject jsonBody = new JSONObject(body);

                            userId = jsonBody.optInt("id", -1);
                            userName = jsonBody.optString("name", "Usuário");

                            Log.d("JWT_DECODE", "ID extraído: " + userId + " / Nome: " + userName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("JWT_ERROR", "Erro ao decodificar token: " + e.getMessage());
                    }

                    if (userId == -1) userId = jsonResponse.optInt("userId", -1);
                    if (userId == -1) userId = jsonResponse.optInt("id", -1);

                    SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putString("salesToken", token)
                            .putInt("savedUserId", userId)
                            .apply();

                    int finalId = userId;
                    String finalName = userName;
                    String finalToken = token;

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(finalId, finalName, finalToken)
                    );

                } else {
                    String errorText = readStream(conn.getErrorStream());
                    String msg = "Erro desconhecido";
                    try {
                        JSONObject jsonError = new JSONObject(errorText);
                        if (jsonError.has("message")) msg = jsonError.getString("message");
                        else if (jsonError.has("error")) msg = jsonError.getString("error");
                    } catch (Exception e) {
                        msg = "Erro " + responseCode;
                    }

                    String finalMsg = msg;
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(finalMsg));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public void validateSession(Context context) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("salesToken", null);

                if (token == null) return;

                URL url = new URL(BuildConfig.BASE_URL + "/auth/verify");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(5000);

                int codigo = conn.getResponseCode();

                if (codigo == 401 || codigo == 403) {
                    SessionManager.forceLogout(context, "Sessão inválida. Por favor, entre novamente.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    private String readStream(java.io.InputStream in) throws java.io.IOException {
        if (in == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }
}