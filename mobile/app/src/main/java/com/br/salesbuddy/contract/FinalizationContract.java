package com.br.salesbuddy.contract;

import android.os.Bundle;
import java.util.List;

public interface FinalizationContract {
    interface View {
        // MUDANÇA: 'items' agora é uma Lista
        void showReceiptData(String name, String cpf, String email, List<String> items,
                             String total, String paid, String change, String saleId);

        void showLoading(String message);
        void hideLoading();
        void showMessage(String message);
        void showError(String error);
        void navigateToNewSale(int userId);
        void closeActivity();
        void showEmailSuccessDialog(String email);
    }

    interface Presenter {
        void loadData(Bundle extras);
        void onSendEmailClicked();
        void onNewSaleClicked();
        void onBackClicked();
    }
}