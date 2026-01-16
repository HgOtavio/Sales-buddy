package com.br.salesbuddy.presenter;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.FinalizationContract;
import com.br.salesbuddy.network.SalesService;

import java.util.Locale;

public class FinalizationPresenter implements FinalizationContract.Presenter {

    private final FinalizationContract.View view;
    private final SalesService service;
    private final Context context;

    // Essa variável precisa ser preenchida no loadData
    private String email;

    private int userId;
    private int realSaleId;

    public FinalizationPresenter(FinalizationContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.service = new SalesService();
    }

    @Override
    public void loadData(Bundle extras) {
        if (extras == null) return;

        this.userId = extras.getInt("ID_DO_LOJISTA", -1);
        this.realSaleId = extras.getInt("ID_VENDA_REAL", -1);

        String nome = extras.getString("NOME", "Cliente");
        String cpf = extras.getString("CPF", "-");


        this.email = extras.getString("EMAIL", "-");

        String item = extras.getString("ITEM", "Diversos");
        double valorVenda = extras.getDouble("VALOR_VENDA", 0.0);
        double valorRecebido = extras.getDouble("VALOR_RECEBIDO", 0.0);

        // Cálculo do troco
        double troco = valorRecebido - valorVenda;
        if (troco < 0) troco = 0;

        String sTotal = String.format(Locale.getDefault(), "R$ %.2f", valorVenda);
        String sPago = String.format(Locale.getDefault(), "R$ %.2f", valorRecebido);
        String sTroco = String.format(Locale.getDefault(), "R$ %.2f", troco);

        String sId = (realSaleId != -1) ? "Venda n° " + realSaleId : "Venda Finalizada";

        view.showReceiptData(nome, cpf, this.email, item, sTotal, sPago, sTroco, sId);
    }

    @Override
    public void onSendEmailClicked() {
        if (realSaleId == -1) {
            view.showError("Erro: ID da venda não identificado.");
            return;
        }

        view.showLoading("Solicitando envio de e-mail...");

        service.dispararEmailBackend(context, realSaleId, new SalesService.SalesCallback() {
            @Override
            public void onSuccess(int ignoredId) {
                view.hideLoading();
                view.showEmailSuccessDialog(email);
            }

            @Override
            public void onError(String message) {
                view.hideLoading();
                view.showError("Erro ao enviar: " + message);
            }
        });
    }

    @Override
    public void onNewSaleClicked() {
        view.navigateToNewSale(userId);
    }

    @Override
    public void onBackClicked() {
        view.closeActivity();
    }
}