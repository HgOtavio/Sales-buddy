package com.br.salesbuddy.contract;

import android.os.Bundle;

public interface FinalizationContract {

    interface View {
        void showReceiptData(String name, String cpf, String email, String item,
                             String total, String paid, String change, String saleId);

        void showLoading(String message);
        void hideLoading();
        void showMessage(String message);
        void showError(String error);

        void showEmailSuccessDialog(String email);

        void navigateToNewSale(int userId);
        void closeActivity();
    }

    interface Presenter {
        void loadData(Bundle extras);
        void onSendEmailClicked();
        void onNewSaleClicked();
        void onBackClicked();
    }
}