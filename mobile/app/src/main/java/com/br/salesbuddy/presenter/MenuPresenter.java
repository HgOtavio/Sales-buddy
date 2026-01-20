package com.br.salesbuddy.presenter;

import com.br.salesbuddy.contract.MenuContract;
import com.br.salesbuddy.model.MenuModel;

public class MenuPresenter implements MenuContract.Presenter {

    private MenuContract.View view;
    private MenuModel model;

    public MenuPresenter(MenuContract.View view) {
        this.view = view;
    }

    @Override
    public void init(int userId, boolean isRegisterScreen) {
        this.model = new MenuModel(userId, isRegisterScreen);

        if (model.isRegisterScreen()) {
            view.setOption1Text("HOME ");
        } else {
            view.setOption1Text("REGISTRAR VENDA");
        }
    }

    @Override
    public void onOption1Clicked() {
        if (model.isRegisterScreen()) {
            view.navigateToHome(model.getUserId());
        } else {
            view.navigateToRegisterSales(model.getUserId());
        }
        view.closeMenu();
    }

    @Override
    public void onOption2Clicked() {
        view.navigateToReprocess(model.getUserId());
        view.closeMenu();
    }

    @Override
    public void onLogoutClicked() {
        view.navigateToLogin();
        view.showMessage("Saiu com sucesso");
        view.closeMenu();
    }

    @Override
    public void onCloseClicked() {
        view.closeMenu();
    }
}