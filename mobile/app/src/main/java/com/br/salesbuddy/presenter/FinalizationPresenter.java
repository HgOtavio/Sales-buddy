package com.br.salesbuddy.presenter;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.FinalizationContract;
import com.br.salesbuddy.network.SalesService; // Supondo que você tenha esse serviço

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinalizationPresenter implements FinalizationContract.Presenter {

    private final FinalizationContract.View view;
    private final Context context;
    private final SalesService service; // Serviço para enviar o email

    // Dados temporários para usar no envio de email/recibo
    private int saleId;
    private int userId;
    private String clientEmail;
    private String clientName;

    public FinalizationPresenter(FinalizationContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.service = new SalesService();
    }

    @Override
    public void loadData(Bundle extras) {
        if (extras == null) {
            view.showError("Dados não encontrados");
            return;
        }

        // 1. Extrair IDs e Dados Básicos
        this.saleId = extras.getInt("ID_VENDA_REAL", 0);
        this.userId = extras.getInt("ID_DO_LOJISTA", -1);
        this.clientName = extras.getString("NOME", "Não Informado");
        String cpf = extras.getString("CPF", "-");
        this.clientEmail = extras.getString("EMAIL", "-");

        // 2. Extrair Valores
        double valVenda = extras.getDouble("VALOR_VENDA", 0.0);
        double valRecebido = extras.getDouble("VALOR_RECEBIDO", 0.0);
        double troco = extras.getDouble("TROCO", 0.0);

        // 3. Formatar Strings
        String totalFmt = String.format(Locale.getDefault(), "R$ %.2f", valVenda);
        String pagoFmt = String.format(Locale.getDefault(), "R$ %.2f", valRecebido);
        String trocoFmt = String.format(Locale.getDefault(), "R$ %.2f", troco);
        String idFmt = (saleId != 0) ? "Venda n° " + saleId : "Venda processada";

        // 4. Processar a Lista de Itens
        // Tenta pegar a lista concatenada ou o item simples
        String rawItems = extras.getString("ITENS_CONCATENADOS");
        if (rawItems == null) rawItems = extras.getString("ITEM");

        List<String> processedItems = processItemsList(rawItems);

        // 5. Enviar para a View
        view.showReceiptData(clientName, cpf, clientEmail, processedItems, totalFmt, pagoFmt, trocoFmt, idFmt);
    }

    /**
     * Lógica que conta itens repetidos.
     * Entrada: "Coxinha, Coxinha, Refri"
     * Saída: ["2x Coxinha", "1x Refri"]
     */
    private List<String> processItemsList(String rawItems) {
        List<String> result = new ArrayList<>();
        if (rawItems == null || rawItems.isEmpty()) {
            result.add("Venda Avulsa / Sem descrição");
            return result;
        }

        Map<String, Integer> counts = new HashMap<>();
        String[] splitItems = rawItems.split(",");

        for (String s : splitItems) {
            String name = s.trim();
            if (!name.isEmpty()) {
                counts.put(name, counts.getOrDefault(name, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            result.add(entry.getValue() + "x " + entry.getKey());
        }

        return result;
    }

    @Override
    public void onSendEmailClicked() {
        if (clientEmail == null || clientEmail.equals("-") || clientEmail.isEmpty()) {
            view.showError("Cliente sem e-mail cadastrado nesta venda.");
            return;
        }

        view.showLoading("Enviando comprovante...");

        // Chamada ao serviço para enviar o e-mail (Ajuste conforme seu SalesService)
        // Exemplo: service.sendReceiptEmail(saleId, email, callback...)

        // --- SIMULAÇÃO DO ENVIO ---
        new android.os.Handler().postDelayed(() -> {
            view.hideLoading();
            view.showEmailSuccessDialog(clientEmail);
        }, 1500);

        /* Se tiver o serviço real implementado:
           service.sendReceipt(saleId, clientEmail, new SalesService.Callback() {
               public void onSuccess() {
                   view.hideLoading();
                   view.showEmailSuccessDialog(clientEmail);
               }
               public void onError(String msg) {
                   view.hideLoading();
                   view.showError(msg);
               }
           });
        */
    }

    @Override
    public void onNewSaleClicked() {
        view.navigateToNewSale(userId);
    }

    @Override
    public void onBackClicked() {
        // Na tela final, voltar geralmente significa "Nova Venda" ou "Home"
        // para evitar voltar para a tela de confirmação e duplicar a venda.
        view.navigateToNewSale(userId);
    }
}