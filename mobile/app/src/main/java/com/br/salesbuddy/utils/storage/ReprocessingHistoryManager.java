package com.br.salesbuddy.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.br.salesbuddy.model.body.ReprocessSaleData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReprocessingHistoryManager {

    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_HISTORY = "processed_reprocessing_history_data";
    private final Context context;
    private final Gson gson;

    public ReprocessingHistoryManager(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void saveItem(ReprocessSaleData item) {
        List<ReprocessSaleData> history = getHistory();

        // Evita duplicatas
        boolean exists = false;
        for (ReprocessSaleData histItem : history) {
            if (histItem.getId() == item.getId()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            item.setProcessed(true);
            history.add(0, item); // Adiciona no topo

            if (history.size() > 50) history = history.subList(0, 50);

            saveList(history);
        }
    }

    public List<ReprocessSaleData> getHistory() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, "");

        if (json.isEmpty()) return new ArrayList<>();

        Type type = new TypeToken<List<ReprocessSaleData>>() {}.getType();
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveList(List<ReprocessSaleData> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(list);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }
}