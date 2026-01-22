package com.br.salesbuddy.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.br.salesbuddy.BuildConfig;
import com.br.salesbuddy.model.SaleData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesService {

    public interface SalesCallback {
        void onSuccess(long idVenda);
        void onError(String message);
    }

    private static final String BASE_URL = BuildConfig.BASE_URL;

    public void sendSale(Context context, SaleData sale, SalesCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("salesToken", null);

                URL url = new URL(BASE_URL + "/vendas");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");

                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);

                JSONObject jsonVenda = new JSONObject();
                jsonVenda.put("userId", sale.userId);
                jsonVenda.put("clientName", sale.nome);
                jsonVenda.put("clientCpf", sale.cpf);
                jsonVenda.put("clientEmail", sale.email);
                jsonVenda.put("saleValue", sale.valorVenda);
                jsonVenda.put("receivedValue", sale.valorRecebido);

                JSONArray arrayItens = new JSONArray();

                if (sale.item != null && !sale.item.isEmpty()) {
                    Map<String, Integer> contagem = new HashMap<>();
                    String[] itensBrutos = sale.item.split(",");

                    for (String s : itensBrutos) {
                        String nomeLimpo = s.trim();
                        if (!nomeLimpo.isEmpty()) {
                            contagem.put(nomeLimpo, contagem.getOrDefault(nomeLimpo, 0) + 1);
                        }
                    }

                    for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
                        JSONObject itemObj = new JSONObject();
                        itemObj.put("productName", entry.getKey());
                        itemObj.put("name", entry.getKey());
                        itemObj.put("quantity", entry.getValue());
                        itemObj.put("unitPrice", 0);

                        arrayItens.put(itemObj);
                    }
                } else {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("productName", "Venda Avulsa");
                    itemObj.put("quantity", 1);
                    itemObj.put("unitPrice", sale.valorVenda);
                    arrayItens.put(itemObj);
                }

                jsonVenda.put("items", arrayItens);
                jsonVenda.put("saleItems", arrayItens);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(jsonVenda.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int codigo = conn.getResponseCode();

                if (codigo == 200 || codigo == 201) {
                    String response = lerStream(conn.getInputStream());
                    JSONObject responseJson = new JSONObject(response);

                    long idGerado = responseJson.optLong("saleId", -1);
                    if(idGerado == -1) idGerado = responseJson.optLong("id", -1);
                    if(idGerado == -1) idGerado = responseJson.optLong("insertId", 0);

                    long finalIdGerado = idGerado;
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalIdGerado));
                } else {
                    String errorBody = lerStream(conn.getErrorStream());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro API: " + codigo + " " + errorBody));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro de Conexão: " + e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public void requestReceiptEmail(Context context, Map<String, Object> saleData, SalesCallback callback) {

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("salesToken", null);

                URL url = new URL(BASE_URL + "/vendas/email");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");

                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);

                JSONObject jsonBody = new JSONObject();

                jsonBody.put("id", saleData.get("id"));
                jsonBody.put("saleId", saleData.get("id"));
                jsonBody.put("clientName", saleData.get("clientName"));
                jsonBody.put("clientCpf", saleData.get("clientCpf"));
                jsonBody.put("email", saleData.get("email"));
                jsonBody.put("clientEmail", saleData.get("email"));
                jsonBody.put("saleValue", saleData.get("saleValue"));
                jsonBody.put("receivedValue", saleData.get("receivedValue"));
                jsonBody.put("change", saleData.get("change"));

                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) saleData.get("items");
                JSONArray jsonItems = new JSONArray();
                if (itemsList != null) {
                    for (Map<String, Object> itemMap : itemsList) {
                        JSONObject itemJson = new JSONObject();
                        Object pName = itemMap.get("productName");
                        if(pName == null) pName = itemMap.get("name");

                        Object pQtd = itemMap.get("quantity");
                        if(pQtd == null) pQtd = itemMap.get("qtd");

                        itemJson.put("productName", pName);
                        itemJson.put("quantity", pQtd);
                        itemJson.put("unitPrice", itemMap.get("unitPrice"));
                        jsonItems.put(itemJson);
                    }
                }
                jsonBody.put("items", jsonItems);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int codigo = conn.getResponseCode();

                if (codigo == 200 || codigo == 201) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(0L));
                } else {
                    String errorBody = lerStream(conn.getErrorStream());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro Backend: " + codigo + " " + errorBody));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Erro de Conexão"));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String lerStream(InputStream in) {
        if (in == null) return "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}