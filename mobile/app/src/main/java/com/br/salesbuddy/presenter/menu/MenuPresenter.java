package com.br.salesbuddy.presenter.menu;

import android.content.Context;

import com.br.salesbuddy.contract.menu.MenuContract;

public class MenuPresenter implements MenuContract.Presenter {

    private final MenuContract.View view;
    private final Context context;

    // Estado local (Substitui o MenuModel desnecessário)
    private int currentUserId;
    private boolean isRegisterScreen;

    public MenuPresenter(MenuContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void init(int userId, boolean isRegisterScreen) {
        this.currentUserId = userId;
        this.isRegisterScreen = isRegisterScreen;

        // Configura o texto do botão dinamicamente
        if (isRegisterScreen) {
            view.setOption1Text("HOME");
        } else {
            view.setOption1Text("REGISTRAR VENDA");
        }
    }

    @Override
    public void onOption1Clicked() {
        if (isRegisterScreen) {
            // Se estou no registro, vou pra Home
            view.navigateToHome(currentUserId);
        } else {
            // Se estou na Home (ou outro), vou pro Registro
            view.navigateToRegisterSales(currentUserId);
        }
        view.closeMenu();
    }

    @Override
    public void onOption2Clicked() {
        view.navigateToReprocess(currentUserId);
        view.closeMenu();
    }

    @Override
    public void onLogoutClicked() {



        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().clear().apply();

        view.showMessage("Saiu com sucesso");
        view.navigateToLogin();
        view.closeMenu();
    }

    @Override
    public void onCloseClicked() {
        view.closeMenu();
    }
}