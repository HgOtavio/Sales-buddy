package com.br.salesbuddy.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.br.salesbuddy.model.SaleData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SalesService {

    public interface SalesCallback {
        void onSuccess(int idVenda);
        void onError(String message);
    }

    public void sendSale(Context context, SaleData sale, SalesCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("salesToken", null);

                URL url = new URL("http://10.0.2.2:3001/vendas");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                conn.setDoOutput(true);

                JSONObject jsonVenda = new JSONObject();
                jsonVenda.put("userId", sale.userId);
                jsonVenda.put("clientName", sale.nome);
                jsonVenda.put("clientCpf", sale.cpf);
                jsonVenda.put("clientEmail", sale.email);
                jsonVenda.put("saleValue", sale.valorVenda);
                jsonVenda.put("receivedValue", sale.valorRecebido);

                JSONArray arrayItens = new JSONArray();
                JSONObject itemObj = new JSONObject();
                String nomeItem = (sale.item == null || sale.item.isEmpty()) ? "Venda App" : sale.item;
                itemObj.put("name", nomeItem);
                itemObj.put("quantity", 1);
                itemObj.put("price", sale.valorVenda);
                arrayItens.put(itemObj);
                jsonVenda.put("items", arrayItens);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonVenda.toString());
                os.flush();
                os.close();

                int codigo = conn.getResponseCode();

                if (codigo == 200 || codigo == 201) {
                    InputStream responseStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);

                    JSONObject responseJson = new JSONObject(sb.toString());
                    int idGerado = responseJson.optInt("saleId", -1);

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(idGerado));
                } else {
                    String errorMsg = "Erro: " + codigo;
                    try {
                        InputStream errorStream = conn.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) sb.append(line);
                            JSONObject jsonError = new JSONObject(sb.toString());
                            if (jsonError.has("missing")) {
                                errorMsg = jsonError.optString("message") + "\nFaltam: R$ " + jsonError.optString("missing");
                            } else {
                                errorMsg = jsonError.optString("message", jsonError.optString("error"));
                            }
                        }
                    } catch (Exception e) {}

                    final String msgFinal = errorMsg;
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(msgFinal));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro de Conexão"));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public void dispararEmailBackend(Context context, int saleId, SalesCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("salesToken", null);

                URL url = new URL("http://10.0.2.2:3001/vendas/email");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("saleId", saleId);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(json.toString());
                os.flush();
                os.close();

                int codigo = conn.getResponseCode();

                if (codigo == 200 || codigo == 201) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(0));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro Backend: " + codigo));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Falha na conexão: " + e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}