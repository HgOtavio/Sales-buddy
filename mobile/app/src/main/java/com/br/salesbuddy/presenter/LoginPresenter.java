package com.br.salesbuddy.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.br.salesbuddy.contract.LoginContract;
import com.br.salesbuddy.model.User;
import com.br.salesbuddy.contract.LoginContract;
import com.br.salesbuddy.network.AuthService;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private AuthService authService;
    private Context context; // Necessário para salvar o token

    public LoginPresenter(LoginContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.authService = new AuthService();
    }

    @Override
    public void performLogin(String user, String password) {
        // 1. Validação simples
        if (user.isEmpty() || password.isEmpty()) {
            view.showLoginError("Preencha todos os campos!");
            return;
        }

        view.showLoading();

        // 2. Chama o "Server" (Service)
        authService.login(user, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess(User userModel) {
                // Salva o Token para usar depois
                SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                prefs.edit().putString("salesToken", userModel.getToken()).apply();

                view.hideLoading();
                view.onLoginSuccess(userModel.getId(), userModel.getName());
            }

            @Override
            public void onError(String errorMessage) {
                view.hideLoading();
                view.showLoginError(errorMessage);
            }
        });
    }
}