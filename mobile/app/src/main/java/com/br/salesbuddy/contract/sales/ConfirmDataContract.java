package com.br.salesbuddy.contract.sales;

import android.os.Bundle;
import java.util.List;

public interface ConfirmDataContract {
    interface View {
        void showLoading();
        void hideLoading();
        void displayData(String nome, String cpf, String email, List<String> items,
                         String valorVenda, String valorRecebido, String troco);
        void showMessage(String message);
        void navigateToConnectionError();
        void showError(String error);

        // Sucesso normal (Venda OK)
        void navigateToFinalization(Bundle finalData);

        // Falha Total (Salvo Localmente)
        void navigateToHome();


        void navigateToReprocessing();
    }

    interface Presenter {
        void loadInitialData(Bundle extras);
        void confirmSale();
    }
}