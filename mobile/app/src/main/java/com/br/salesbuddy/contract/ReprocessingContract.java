package com.br.salesbuddy.contract;
import com.br.salesbuddy.model.ReprocessSaleData;
import java.util.List;

public interface ReprocessingContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showList(List<ReprocessSaleData> list);
        void showEmptyState();
        void showMessage(String message);
        void removeItemFromList(int id);
        void clearList(); // NOVO
        void navigateBack();
    }

    interface Presenter {
        void loadPendingSales(int userId);
        void onItemClicked(int id);
        void onReprocessAllClicked(); // NOVO
        void onBackClicked();
    }
}