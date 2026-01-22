package com.br.salesbuddy.utils; // Ou seu pacote preferido

import android.content.Context;
import android.content.SharedPreferences;
import com.br.salesbuddy.model.SaleData;
import org.json.JSONObject;

public class SalePersistence {

    private static final String PREF_NAME = "sales_backup";
    private static final String KEY_PENDING_SALE = "pending_sale_json";

    // Salva a venda no LocalStorage
    public static void saveSale(Context context, SaleData sale) {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", sale.userId);
            json.put("nome", sale.nome);
            json.put("cpf", sale.cpf);
            json.put("email", sale.email);
            json.put("item", sale.item); // String concatenada ou itens brutos
            json.put("valorVenda", sale.valorVenda);
            json.put("valorRecebido", sale.valorRecebido);

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_PENDING_SALE, json.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SaleData getSavedSale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_PENDING_SALE, null);

        if (jsonString == null) return null;

        try {
            JSONObject json = new JSONObject(jsonString);
            SaleData sale = new SaleData();

            sale.userId = json.optInt("userId", -1);
            sale.nome = json.optString("nome");
            sale.cpf = json.optString("cpf");
            sale.email = json.optString("email");
            sale.item = json.optString("item");
            sale.valorVenda = json.optDouble("valorVenda", 0.0);
            sale.valorRecebido = json.optDouble("valorRecebido", 0.0);

            return sale;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Limpa os dados (usar quando a venda for sucesso)
    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_PENDING_SALE).apply();
    }

    // Verifica se tem algo salvo
    public static boolean hasPendingSale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_PENDING_SALE);
    }
}