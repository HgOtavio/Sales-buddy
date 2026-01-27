package com.br.salesbuddy.contract.home;

public interface HomeContract {

    interface View {
        void navigateToRegisterSales(int userId);
        void navigateToReprocessing(int userId);
        void showMenu(int userId);
        void navigateToConnectionError();
    }

    interface Presenter {
        void setUserId(int userId);
        void onRegisterSalesClicked();
        void onReprocessClicked();
        void onMenuClicked();
    }
}