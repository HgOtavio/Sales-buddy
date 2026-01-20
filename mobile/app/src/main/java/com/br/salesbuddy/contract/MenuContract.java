package com.br.salesbuddy.contract;

public interface MenuContract {

    interface View {
        void setOption1Text(String text);
        void navigateToHome(int userId);
        void navigateToRegisterSales(int userId);
        void navigateToReprocess(int userId);
        void navigateToLogin();
        void showMessage(String message);
        void navigateToConnectionError();
        void closeMenu();
    }

    interface Presenter {
        void init(int userId, boolean isRegisterScreen);
        void onOption1Clicked();
        void onOption2Clicked();
        void onLogoutClicked();
        void onCloseClicked();
    }
}