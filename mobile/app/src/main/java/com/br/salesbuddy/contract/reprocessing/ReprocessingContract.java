package com.br.salesbuddy.contract.reprocessing;

import com.br.salesbuddy.model.body.ReprocessSaleData;
import java.util.List;

public interface ReprocessingContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showList(List<ReprocessSaleData> list);
        void showEmptyState();
        void navigateToConnectionError();

        void showMessage(String message); // Usado para Sucesso
        void showError(String error);     // Usado para Toast padrão


        void showErrorDialog(String title, String message);

        void markItemAsProcessed(int index);

        void removeItemFromList(int id);
        void clearList();
        void navigateBack();
    }

    interface Presenter {
        void loadPendingSales(int userId);
        void onItemClicked(int id);
        void onReprocessAllClicked();
        void onBackClicked();
    }
}