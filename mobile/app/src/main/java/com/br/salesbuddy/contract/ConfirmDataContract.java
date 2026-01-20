package com.br.salesbuddy.contract;

import android.os.Bundle;
import java.util.List;

public interface ConfirmDataContract {
    interface View {
        void showLoading();
        void hideLoading();
        // MUDANÇA: 'item' agora é 'List<String> items'
        void displayData(String nome, String cpf, String email, List<String> items,
                         String valorVenda, String valorRecebido, String troco);
        void showMessage(String message);
        void showError(String error);
        void navigateToFinalization(Bundle finalData);
    }

    interface Presenter {
        void loadInitialData(Bundle extras);
        void confirmSale();
    }
}