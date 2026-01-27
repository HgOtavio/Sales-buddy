package com.br.salesbuddy.model.mapper;

import com.br.salesbuddy.model.body.SaleData;
import com.br.salesbuddy.model.request.ReprocessingRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleMapper {

    public static ReprocessingRequest toReprocessingRequest(SaleData data, String reason) {
        ReprocessingRequest req = new ReprocessingRequest();
        req.setUserId(data.userId);
        req.setClientName(data.nome);
        req.setSaleValue(data.valorVenda);
        req.setReceivedValue(data.valorRecebido);
        req.setChange(data.valorRecebido - data.valorVenda);
        req.setErrorReason(reason != null ? reason : "Erro desconhecido");

        List<ReprocessingRequest.ReprocessItemRequest> items = new ArrayList<>();

        if (data.item != null && !data.item.isEmpty()) {
            Map<String, Integer> contagem = new HashMap<>();
            String[] rawArray = data.item.split(",");
            for (String s : rawArray) {
                String nome = s.trim();
                if (!nome.isEmpty()) {
                    contagem.put(nome, contagem.getOrDefault(nome, 0) + 1);
                }
            }

            int totalCount = Math.max(1, rawArray.length);
            double precoMedio = data.valorVenda / totalCount;

            for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
                double totalItemPrice = precoMedio * entry.getValue();
                items.add(new ReprocessingRequest.ReprocessItemRequest(
                        entry.getKey(),
                        entry.getValue(),
                        precoMedio,
                        totalItemPrice
                ));
            }
        } else {
            items.add(new ReprocessingRequest.ReprocessItemRequest("Venda Avulsa", 1, data.valorVenda, data.valorVenda));
        }

        req.setItems(items);
        return req;
    }
}