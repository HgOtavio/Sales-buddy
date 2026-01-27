package com.br.salesbuddy.model.request;

public class LoginRequest {
    public String user;
    public String password;

    public LoginRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }
}