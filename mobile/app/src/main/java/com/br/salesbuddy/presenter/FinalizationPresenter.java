package com.br.salesbuddy.presenter;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.FinalizationContract;
import com.br.salesbuddy.network.SalesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinalizationPresenter implements FinalizationContract.Presenter {

    private final FinalizationContract.View view;
    private final Context context;
    private final SalesService service;

    private long saleId;

    private int userId;
    private String clientName;
    private String clientCpf;
    private String clientEmail;

    private double valVenda;
    private double valRecebido;
    private double troco;

    private String rawItemsString;

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

        this.saleId = extras.getLong("ID_VENDA_REAL", 0L);

        this.userId = extras.getInt("ID_DO_LOJISTA", -1);
        this.clientName = extras.getString("NOME", "Não Informado");
        this.clientCpf = extras.getString("CPF", "-");
        this.clientEmail = extras.getString("EMAIL", "-");

        this.valVenda = extras.getDouble("VALOR_VENDA", 0.0);
        this.valRecebido = extras.getDouble("VALOR_RECEBIDO", 0.0);
        this.troco = extras.getDouble("TROCO", 0.0);

        String totalFmt = String.format(Locale.getDefault(), "R$ %.2f", valVenda);
        String pagoFmt = String.format(Locale.getDefault(), "R$ %.2f", valRecebido);
        String trocoFmt = String.format(Locale.getDefault(), "R$ %.2f", troco);

        String idFmt = (saleId != 0L) ? "Venda n° " + saleId : "Venda processada";

        this.rawItemsString = extras.getString("ITENS_CONCATENADOS");
        if (rawItemsString == null) rawItemsString = extras.getString("ITEM");

        List<String> processedItems = processItemsForDisplay(rawItemsString);

        view.showReceiptData(clientName, clientCpf, clientEmail, processedItems, totalFmt, pagoFmt, trocoFmt, idFmt);
    }

    private List<String> processItemsForDisplay(String rawItems) {
        List<String> result = new ArrayList<>();
        if (rawItems == null || rawItems.isEmpty()) {
            result.add("Venda Avulsa");
            return result;
        }
        Map<String, Integer> counts = new HashMap<>();
        for (String s : rawItems.split(",")) {
            String name = s.trim();
            if (!name.isEmpty()) counts.put(name, counts.getOrDefault(name, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            result.add(entry.getValue() + "x " + entry.getKey());
        }
        return result;
    }

    @Override
    public void onSendEmailClicked() {
        if (clientEmail == null || clientEmail.equals("-") || clientEmail.isEmpty() || !clientEmail.contains("@")) {
            view.showError("E-mail inválido ou não informado.");
            return;
        }

        if (saleId == 0L) {
            view.showError("Erro: ID da venda inválido. Não é possível enviar o comprovante.");
            return;
        }

        view.showLoading("Enviando comprovante...");



        Map<String, Object> requestData = new HashMap<>();
        requestData.put("saleId", saleId); // Passando o ID correto

        service.requestReceiptEmail(context, requestData, new SalesService.SalesCallback() {
            @Override
            public void onSuccess(long id) {
                view.hideLoading();
                view.showEmailSuccessDialog(clientEmail);
            }

            @Override
            public void onError(String message) {
                view.hideLoading();
                if (isNetworkError(message)) {
                    view.navigateToConnectionError();
                } else {
                    view.showError("Falha ao enviar: " + message);
                }
            }
        });
    }

    @Override
    public void onNewSaleClicked() {
        view.navigateToNewSale(userId);
    }

    @Override
    public void onBackClicked() {
        view.navigateToNewSale(userId);
    }

    private boolean isNetworkError(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase();
        return m.contains("unable to resolve host") || m.contains("timeout") || m.contains("connect") || m.contains("network");
    }
}