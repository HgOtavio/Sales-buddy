package com.br.salesbuddy.utils.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptItemHelper {

    public static List<String> processItemsForDisplay(String rawItems) {
        List<String> result = new ArrayList<>();
        if (rawItems == null || rawItems.trim().isEmpty()) {
            result.add("Venda Avulsa / Sem itens");
            return result;
        }

        Map<String, Integer> counts = new HashMap<>();
        // Separa por vírgula e conta repetições
        for (String s : rawItems.split(",")) {
            String name = s.trim();
            if (!name.isEmpty()) {
                counts.put(name, counts.getOrDefault(name, 0) + 1);
            }
        }

        // Formata "2x Coca Cola"
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            result.add(entry.getValue() + "x " + entry.getKey());
        }
        return result;
    }
}