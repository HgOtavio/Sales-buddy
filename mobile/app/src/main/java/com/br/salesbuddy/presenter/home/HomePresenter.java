package com.br.salesbuddy.presenter.home;

import com.br.salesbuddy.contract.home.HomeContract;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;
    private int userId;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void onRegisterSalesClicked() {
        view.navigateToRegisterSales(userId);
    }

    @Override
    public void onReprocessClicked() {
        view.navigateToReprocessing(userId);
    }

    @Override
    public void onMenuClicked() {
        view.showMenu(userId);
    }
}