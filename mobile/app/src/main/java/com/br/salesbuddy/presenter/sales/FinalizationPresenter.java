package com.br.salesbuddy.presenter.sales;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.sales.FinalizationContract;
import com.br.salesbuddy.network.service.SalesService;
import com.br.salesbuddy.utils.parser.FinalizationBundleParser; // <--- NOVO
import com.br.salesbuddy.utils.format.ReceiptItemHelper;      // <--- NOVO

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinalizationPresenter implements FinalizationContract.Presenter {

    private final FinalizationContract.View view;
    private final Context context;
    private final SalesService service;

    // Guardamos o objeto de dados inteiro
    private FinalizationBundleParser.FinalizationData data;

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

        // 1. Usa o Parser para extrair tudo de uma vez
        this.data = FinalizationBundleParser.parse(extras);

        // 2. Formatação para exibição
        String totalFmt = String.format(Locale.getDefault(), "R$ %.2f", data.valVenda);
        String pagoFmt = String.format(Locale.getDefault(), "R$ %.2f", data.valRecebido);
        String trocoFmt = String.format(Locale.getDefault(), "R$ %.2f", data.troco);

        // Verifica se é venda real ou offline
        String idFmt = (data.saleId > 0) ? "Venda n° " + data.saleId : "Venda Salva (Pendente)";

        // 3. Usa o Helper para processar a lista de itens
        List<String> processedItems = ReceiptItemHelper.processItemsForDisplay(data.rawItems);

        view.showReceiptData(data.clientName, data.clientCpf, data.clientEmail,
                processedItems, totalFmt, pagoFmt, trocoFmt, idFmt);
    }

    @Override
    public void onSendEmailClicked() {
        if (data == null) return;

        if (data.clientEmail == null || !data.clientEmail.contains("@")) {
            view.showError("E-mail inválido ou não informado.");
            return;
        }

        // Bloqueia envio de email para vendas offline (ID 0 ou -1)
        if (data.saleId <= 0) {
            view.showError("Venda Offline. O comprovante será enviado após a sincronização.");
            return;
        }

        view.showLoading("Enviando comprovante...");

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("saleId", data.saleId);

        service.requestReceiptEmail(context, requestData, new SalesService.SalesCallback() {
            @Override
            public void onSuccess(long id) {
                view.hideLoading();
                view.showEmailSuccessDialog(data.clientEmail);
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
        if (data != null) {
            view.navigateToNewSale(data.userId);
        } else {
            // Fallback caso dados tenham se perdido (raro)
            view.closeActivity();
        }
    }

    @Override
    public void onBackClicked() {
        if (data != null) {
            view.navigateToNewSale(data.userId);
        } else {
            view.closeActivity();
        }
    }

    private boolean isNetworkError(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase();
        return m.contains("unable to resolve host") || m.contains("timeout") ||
                m.contains("connect") || m.contains("network");
    }
}