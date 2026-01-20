package com.br.salesbuddy.presenter;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.ConfirmDataContract;
import com.br.salesbuddy.model.SaleData;
import com.br.salesbuddy.network.SalesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfirmDataPresenter implements ConfirmDataContract.Presenter {

    private final ConfirmDataContract.View view;
    private final SalesService service;
    private final SaleData currentSale;
    private final Context context;

    public ConfirmDataPresenter(ConfirmDataContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.service = new SalesService();
        this.currentSale = new SaleData();
    }

    @Override
    public void loadInitialData(Bundle extras) {
        if (extras == null) {
            view.showError("Dados da venda não encontrados.");
            return;
        }

        // 1. Extrair dados
        currentSale.userId = extras.getInt("ID_DO_LOJISTA", -1);
        currentSale.nome = extras.getString("NOME");
        currentSale.cpf = extras.getString("CPF");
        currentSale.email = extras.getString("EMAIL");

        // Pega a string bruta de itens (separada por vírgula)
        String rawItems = extras.getString("ITENS_CONCATENADOS");
        if (rawItems == null) rawItems = extras.getString("ITEM"); // Fallback
        currentSale.item = rawItems;

        // 2. Converter valores
        try {
            String strVenda = extras.getString("VALOR_VENDA", "0")
                    .replace("R$", "").replace(".", "").replace(",", ".").trim();
            String strReceb = extras.getString("VALOR_RECEBIDO", "0")
                    .replace("R$", "").replace(".", "").replace(",", ".").trim();

            currentSale.valorVenda = Double.parseDouble(strVenda);
            currentSale.valorRecebido = Double.parseDouble(strReceb);
        } catch (Exception e) {
            currentSale.valorVenda = 0.0;
            currentSale.valorRecebido = 0.0;
        }

        // 3. Cálculos e Formatação
        double troco = currentSale.valorRecebido - currentSale.valorVenda;
        if (troco < 0) troco = 0.0;

        String nomeDisplay = (currentSale.nome == null || currentSale.nome.isEmpty()) ? "Não informado" : currentSale.nome;
        String cpfDisplay = (currentSale.cpf == null || currentSale.cpf.isEmpty()) ? "-" : currentSale.cpf;
        String emailDisplay = (currentSale.email == null || currentSale.email.isEmpty()) ? "-" : currentSale.email;

        String vendaFmt = String.format(Locale.getDefault(), "R$ %.2f", currentSale.valorVenda);
        String recebidoFmt = String.format(Locale.getDefault(), "R$ %.2f", currentSale.valorRecebido);
        String trocoFmt = String.format(Locale.getDefault(), "R$ %.2f", troco);

        // --- LÓGICA DE PROCESSAMENTO DOS ITENS ---
        List<String> listaFormatada = processarItens(rawItems);

        // 4. Envia para a View
        view.displayData(nomeDisplay, cpfDisplay, emailDisplay, listaFormatada, vendaFmt, recebidoFmt, trocoFmt);
    }

    /**
     * Transforma "Coxinha, Coxinha, Coca" em ["2x Coxinha", "1x Coca"]
     */
    private List<String> processarItens(String rawItems) {
        List<String> resultado = new ArrayList<>();
        if (rawItems == null || rawItems.isEmpty()) {
            resultado.add("Venda Avulsa / Sem itens");
            return resultado;
        }

        Map<String, Integer> contagem = new HashMap<>();
        String[] itensArray = rawItems.split(",");

        for (String item : itensArray) {
            String nome = item.trim();
            if (!nome.isEmpty()) {
                contagem.put(nome, contagem.getOrDefault(nome, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            resultado.add(entry.getValue() + "x " + entry.getKey());
        }
        return resultado;
    }

    @Override
    public void confirmSale() {
        view.showLoading();
        service.sendSale(context, currentSale, new SalesService.SalesCallback() {
            @Override
            public void onSuccess(int idVenda) {
                view.hideLoading();
                view.showMessage("Venda Finalizada!");

                Bundle finalBundle = new Bundle();
                finalBundle.putInt("ID_VENDA_REAL", idVenda);
                finalBundle.putInt("ID_DO_LOJISTA", currentSale.userId);
                finalBundle.putString("NOME", currentSale.nome);
                finalBundle.putString("CPF", currentSale.cpf);
                finalBundle.putString("EMAIL", currentSale.email);
                finalBundle.putDouble("VALOR_VENDA", currentSale.valorVenda);
                finalBundle.putDouble("VALOR_RECEBIDO", currentSale.valorRecebido);
                double troco = currentSale.valorRecebido - currentSale.valorVenda;
                finalBundle.putDouble("TROCO", troco < 0 ? 0.0 : troco);

                view.navigateToFinalization(finalBundle);
            }

            @Override
            public void onError(String message) {
                view.hideLoading();
                view.showError("Erro: " + message);
            }
        });
    }
}