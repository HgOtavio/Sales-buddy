package com.br.salesbuddy.contract.splash;

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