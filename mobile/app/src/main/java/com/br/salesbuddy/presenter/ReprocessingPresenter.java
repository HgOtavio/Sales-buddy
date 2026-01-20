package com.br.salesbuddy.presenter;

import android.os.Handler;
import com.br.salesbuddy.contract.ReprocessingContract;
import com.br.salesbuddy.model.ReprocessSaleData;

import java.util.ArrayList;
import java.util.List;

public class ReprocessingPresenter implements ReprocessingContract.Presenter {

    private final ReprocessingContract.View view;
    // Guardamos a lista aqui para saber o que reprocessar
    private List<ReprocessSaleData> currentList = new ArrayList<>();

    public ReprocessingPresenter(ReprocessingContract.View view) {
        this.view = view;
    }

    @Override
    public void loadPendingSales(int userId) {
        view.showLoading();

        // Simulando carregamento da API
        new Handler().postDelayed(() -> {
            view.hideLoading();

            currentList.clear();
            currentList.add(new ReprocessSaleData(1, "Mercadinho João", 150.00, "Erro Rede", "19/01"));
            currentList.add(new ReprocessSaleData(2, "Padaria Central", 50.00, "Timeout", "18/01"));

            if (currentList.isEmpty()) {
                view.showEmptyState();
            } else {
                view.showList(currentList);
            }
        }, 1000);
    }

    // --- MUDANÇA AQUI: Ação do Botão Grande ---
    @Override
    public void onReprocessAllClicked() {
        if (currentList.isEmpty()) {
            // Se não tem nada na lista, não faz nada ou avisa
            return;
        }

        view.showLoading();

        // Simulando envio para API (POST /reprocess/all ou loop)
        new Handler().postDelayed(() -> {
            view.hideLoading();

            // Sucesso!
            view.showMessage("Reprocessamento efetuado com sucesso");

            // Limpa a lista pois já foi processado
            currentList.clear();
            view.clearList();
            view.showEmptyState();

        }, 2000);
    }

    @Override
    public void onBackClicked() {
        view.navigateBack();
    }

    // Método antigo onItemClicked pode ser removido se não for usar mais
    @Override
    public void onItemClicked(int id) {}
}