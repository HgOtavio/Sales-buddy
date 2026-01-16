package com.br.salesbuddy.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.br.salesbuddy.model.User;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    // Interface para devolver a resposta (Callback)
    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public void login(String user, String password, LoginCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // SEU SERVIDOR NODE.JS (10.0.2.2 é o localhost do PC visto pelo emulador)
                URL url = new URL("http://10.0.2.2:3001/auth/login");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                // JSON de envio
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user", user);       // Chave exata do backend
                jsonParam.put("password", password); // Chave exata do backend

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    // SUCESSO
                    String responseText = readStream(conn.getInputStream());
                    JSONObject jsonResponse = new JSONObject(responseText);

                    String token = jsonResponse.getString("token");
                    // O backend retorna { token: "...", user: { id: 1, name: "..." } }
                    JSONObject userObj = jsonResponse.getJSONObject("user");

                    User userModel = new User(
                            userObj.getInt("id"),
                            userObj.getString("name"),
                            token
                    );

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(userModel));

                } else {
                    // ERRO (400, 401, 404)
                    String errorText = readStream(conn.getErrorStream());
                    JSONObject jsonError = new JSONObject(errorText);
                    // Pega a mensagem customizada que criamos no backend
                    String msg = jsonError.optString("message", "Erro desconhecido");

                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(msg));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
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