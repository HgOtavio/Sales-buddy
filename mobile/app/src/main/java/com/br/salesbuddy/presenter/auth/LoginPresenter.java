package com.br.salesbuddy.presenter.auth;

import android.content.Context;

import com.br.salesbuddy.contract.auth.LoginContract;
import com.br.salesbuddy.network.service.AuthService;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final AuthService authService;
    private final Context context;

    public LoginPresenter(LoginContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.authService = new AuthService();
    }

    @Override
    public void performLogin(String email, String password) {

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            view.showLoginError("Preencha todos os campos!");
            return;
        }

        view.showLoading();


        authService.login(context, email.trim(), password.trim(), new AuthService.LoginCallback() {
            @Override
            public void onSuccess(int userId, String userName, String token) {
                view.hideLoading();

                view.onLoginSuccess(userId, userName);
            }

            @Override
            public void onError(String errorMessage) {
                view.hideLoading();
                view.showLoginError(errorMessage);
            }
        });
    }
}