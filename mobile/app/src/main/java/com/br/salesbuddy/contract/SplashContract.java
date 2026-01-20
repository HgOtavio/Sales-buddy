package com.br.salesbuddy.contract;

public interface SplashContract {
    interface View {
        void navigateToLogin();
    }

    interface Presenter {
        void startSplashLogic();
        void destroy();
    }
}