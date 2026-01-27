package com.br.salesbuddy.utils.parser;

import android.os.Bundle;

public class SaleBundleFactory {

    public static Bundle create(int userId, String nome, String cpf, String email,
                                double valVenda, double valRecebido, String itens) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID_DO_LOJISTA", userId);
        bundle.putString("NOME", nome);
        bundle.putString("CPF", cpf);
        bundle.putString("EMAIL", email);

        // Regra de negócio: Se não tiver itens, chama de "Item Diverso"
        String itensFinal = (itens == null || itens.trim().isEmpty()) ? "Item Diverso" : itens;
        bundle.putString("ITENS_CONCATENADOS", itensFinal);

        bundle.putDouble("VALOR_VENDA", valVenda);
        bundle.putDouble("VALOR_RECEBIDO", valRecebido);

        return bundle;
    }
}