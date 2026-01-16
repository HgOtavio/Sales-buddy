package com.br.salesbuddy.contract; // <--- PASTA SOLICITADA

import android.os.Bundle;

public interface ConfirmDataContract {

    interface View {
        void showLoading();
        void hideLoading();

        // Exibe os dados formatados na tela
        void displayData(String nome, String cpf, String email, String item,
                         String valorVenda, String valorRecebido, String troco);

        void showMessage(String message); // Toast
        void showError(String error);

        void navigateToFinalization(Bundle finalData);
    }

    interface Presenter {
        // Recebe os dados da Intent e processa
        void loadInitialData(Bundle extras);

        // Ação do botão confirmar
        void confirmSale();
    }
}