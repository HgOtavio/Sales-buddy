package com.br.salesbuddy.model;

public class MenuModel {
    private int userId;
    private boolean isRegisterScreen;

    public MenuModel(int userId, boolean isRegisterScreen) {
        this.userId = userId;
        this.isRegisterScreen = isRegisterScreen;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isRegisterScreen() {
        return isRegisterScreen;
    }
}