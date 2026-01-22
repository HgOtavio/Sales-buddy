package com.br.salesbuddy.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.br.salesbuddy.contract.LoginContract;
import com.br.salesbuddy.network.AuthService;

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

        if (email.isEmpty() || password.isEmpty()) {
            view.showLoginError("Preencha todos os campos!");
            return;
        }

        view.showLoading();


        authService.login(context, email, password, createLoginCallback());
    }


    private AuthService.LoginCallback createLoginCallback() {
        return new AuthService.LoginCallback() {
            @Override
            public void onSuccess(int userId, String userName, String token) {

                if (view == null) return;

                view.hideLoading();


                saveTokenLocally(token);


                view.onLoginSuccess(userId, userName);
            }

            @Override
            public void onError(String errorMessage) {
                if (view == null) return;

                view.hideLoading();
                view.showLoginError(errorMessage);
            }
        };
    }

    private void saveTokenLocally(String token) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("salesToken", token).apply();
    }
}