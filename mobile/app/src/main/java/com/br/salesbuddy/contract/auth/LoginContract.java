package com.br.salesbuddy.contract.auth;

public interface LoginContract {
    // O que a Tela (Activity) precisa fazer
    interface View {
        void showLoading();
        void hideLoading();
        void showLoginError(String message);
        void onLoginSuccess(int userId, String userName);
        void navigateToConnectionError();
    }

    // O que o Presenter (Lógica) precisa fazer
    interface Presenter {
        void performLogin(String user, String password);
    }
}