package com.br.salesbuddy.utils.parser;

import android.os.Bundle;
import com.br.salesbuddy.model.body.SaleData;

public class SaleDataParser {

    public static SaleData fromBundle(Bundle extras) {
        SaleData sale = new SaleData();
        sale.userId = extras.getInt("ID_DO_LOJISTA", -1);
        sale.nome = extras.getString("NOME");
        sale.cpf = extras.getString("CPF");
        sale.email = extras.getString("EMAIL");

        String rawItems = extras.getString("ITENS_CONCATENADOS");
        if (rawItems == null) rawItems = extras.getString("ITEM");
        sale.item = rawItems;

        try {
            sale.valorVenda = parseDouble(extras.get("VALOR_VENDA"));
            sale.valorRecebido = parseDouble(extras.get("VALOR_RECEBIDO"));
        } catch (Exception e) {
            sale.valorVenda = 0.0;
            sale.valorRecebido = 0.0;
        }
        return sale;
    }

    private static double parseDouble(Object value) {
        if (value instanceof Double) return (Double) value;
        if (value instanceof String) {
            String str = ((String) value).replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();
            return Double.parseDouble(str);
        }
        return 0.0;
    }
}