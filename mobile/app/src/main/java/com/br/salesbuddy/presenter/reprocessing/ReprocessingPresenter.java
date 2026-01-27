package com.br.salesbuddy.presenter.reprocessing;

import android.content.Context;

import com.br.salesbuddy.contract.reprocessing.ReprocessingContract;
import com.br.salesbuddy.model.body.ReprocessSaleData;
import com.br.salesbuddy.model.request.ApproveRequest;
import com.br.salesbuddy.model.request.ReprocessingRequest;
import com.br.salesbuddy.model.response.ReprocessingResponse;
import com.br.salesbuddy.network.helper.ApiErrorHelper; // Use aquele Helper que criamos antes
import com.br.salesbuddy.network.config.RetrofitClient;
import com.br.salesbuddy.network.api.ReprocessingApi;
import com.br.salesbuddy.utils.storage.ReprocessingHistoryManager; // <--- NOVO
import com.br.salesbuddy.model.mapper.ReprocessingMapper; // <--- NOVO

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReprocessingPresenter implements ReprocessingContract.Presenter {

    private final ReprocessingContract.View view;
    private final ReprocessingApi api;
    private final Context context;
    private final ReprocessingHistoryManager historyManager; // Gerencia o SharedPreferences

    private List<ReprocessSaleData> currentList = new ArrayList<>();

    public ReprocessingPresenter(ReprocessingContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.api = RetrofitClient.getClient().create(ReprocessingApi.class);
        this.historyManager = new ReprocessingHistoryManager(context); // Inicializa o gerenciador
    }

    @Override
    public void loadPendingSales(int userId) {
        view.showLoading();

        String token = getToken(); // Mantenha seu método privado getToken() lá embaixo
        if (token.isEmpty()) {
            view.hideLoading();
            view.showError("Sessão inválida.");
            return;
        }

        ReprocessingRequest request = new ReprocessingRequest();
        request.setUserId(userId);

        api.listReprocessing("Bearer " + token, request).enqueue(new Callback<List<ReprocessingResponse>>() {
            @Override
            public void onResponse(Call<List<ReprocessingResponse>> call, Response<List<ReprocessingResponse>> response) {
                view.hideLoading();
                currentList.clear();

                // 1. Pega da API e Converte usando o Mapper
                if (response.isSuccessful() && response.body() != null) {
                    List<ReprocessSaleData> apiItems = ReprocessingMapper.toUiModel(response.body());
                    currentList.addAll(apiItems);
                }

                // 2. Pega do Histórico Local usando o Manager
                List<ReprocessSaleData> localHistory = historyManager.getHistory();
                currentList.addAll(localHistory);

                updateListOnView();
            }

            @Override
            public void onFailure(Call<List<ReprocessingResponse>> call, Throwable t) {
                view.hideLoading();

                // Fallback: Se falhar a net, tenta mostrar o histórico
                List<ReprocessSaleData> localHistory = historyManager.getHistory();
                if (!localHistory.isEmpty()) {
                    currentList.clear();
                    currentList.addAll(localHistory);
                    updateListOnView();
                } else {
                    view.navigateToConnectionError();
                }
            }
        });
    }

    private void updateListOnView() {
        if (currentList.isEmpty()) {
            view.showEmptyState();
        } else {
            view.showList(currentList);
        }
    }

    @Override
    public void onReprocessAllClicked() {
        if (currentList.isEmpty()) return;

        String token = getToken();
        if (token.isEmpty()) return;

        view.showLoading();
        processNextItem(0, token);
    }

    private void processNextItem(int index, String token) {
        if (index >= currentList.size()) {
            view.hideLoading();
            view.showMessage("Reprocessamento finalizado!");
            return;
        }

        ReprocessSaleData item = currentList.get(index);

        // Se já processado, pula
        if (item.isProcessed()) {
            processNextItem(index + 1, token);
            return;
        }

        api.approveReprocessing("Bearer " + token, new ApproveRequest(item.getId())).enqueue(new Callback<ReprocessingResponse>() {
            @Override
            public void onResponse(Call<ReprocessingResponse> call, Response<ReprocessingResponse> response) {
                if (response.isSuccessful()) {
                    // SUCESSO!
                    view.markItemAsProcessed(index);
                    item.setProcessed(true);

                    // Salva no histórico usando o Manager
                    historyManager.saveItem(item);

                    // Recursão para o próximo
                    processNextItem(index + 1, token);
                } else {
                    view.hideLoading();
                    // Usa o Helper de erro se tiver, ou sua lógica antiga
                    String errorMsg = ApiErrorHelper.parseError(response);
                    view.showErrorDialog("Erro no Item", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ReprocessingResponse> call, Throwable t) {
                view.hideLoading();
                view.navigateToConnectionError();
            }
        });
    }

    private String getToken() {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("salesToken", "");
    }

    @Override public void onBackClicked() { view.navigateBack(); }
    @Override public void onItemClicked(int id) {}
}