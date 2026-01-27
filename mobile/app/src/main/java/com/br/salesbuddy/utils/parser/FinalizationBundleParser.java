package com.br.salesbuddy.utils.parser;

import android.os.Bundle;

// Classe interna simples para transportar os dados
public class FinalizationBundleParser {

    public static class FinalizationData {
        public long saleId;
        public int userId;
        public String clientName, clientCpf, clientEmail;
        public double valVenda, valRecebido, troco;
        public String rawItems;
    }

    public static FinalizationData parse(Bundle extras) {
        FinalizationData data = new FinalizationData();
        if (extras == null) return data;

        data.saleId = extras.getLong("ID_VENDA_REAL", 0L);
        data.userId = extras.getInt("ID_DO_LOJISTA", -1);
        data.clientName = extras.getString("NOME", "Não Informado");
        data.clientCpf = extras.getString("CPF", "-");
        data.clientEmail = extras.getString("EMAIL", "-");

        data.valVenda = extras.getDouble("VALOR_VENDA", 0.0);
        data.valRecebido = extras.getDouble("VALOR_RECEBIDO", 0.0);
        data.troco = extras.getDouble("TROCO", 0.0);

        String items = extras.getString("ITENS_CONCATENADOS");
        if (items == null) items = extras.getString("ITEM");
        data.rawItems = items;

        return data;
    }
}