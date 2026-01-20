package com.br.salesbuddy.contract;

public interface SplashContract {
    interface View {
        void navigateToLogin();
        void navigateToConnectionError();
    }

    interface Presenter {
        void startSplashLogic();
        void destroy();

    }
}