package com.br.salesbuddy.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    public String token;

    @SerializedName("userId")
    public Integer userId;

    @SerializedName("id")
    public Integer id;

    // Campos para capturar o erro da API (caso venha no corpo da resposta)
    public String message;
    public String error;
}