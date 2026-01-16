package com.br.salesbuddy.contract;

import android.os.Bundle;

public interface RegisterSalesContract {

    interface View {
        // UI Actions
        void setupMasks(); // Aplica as máscaras nos EditTexts
        void showInputError(String message);

        // Dynamic Items
        void addDynamicItemRow();
        void removeDynamicItemRow(android.view.View view);
        void convertLastButtonToMinus(android.view.View lastView);

        // Navigation
        void navigateToConfirm(Bundle bundle);
    }

    interface Presenter {
        void validateAndAdvance(int userId, String nome, String cpf, String email,
                                String valVenda, String valRecebido, String itensConcatenados);
    }
}