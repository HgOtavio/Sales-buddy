package com.br.salesbuddy.model;

import com.google.gson.annotations.SerializedName;

public class ReprocessingResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("saleId")
    private int saleId;

    @SerializedName("status")
    private String status;

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status) || saleId > 0;
    }

    public String getMessage() {
        return message;
    }
}