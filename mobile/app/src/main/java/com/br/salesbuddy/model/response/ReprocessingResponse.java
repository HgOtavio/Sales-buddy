package com.br.salesbuddy.model.response;

import com.google.gson.annotations.SerializedName;

public class ReprocessingResponse {

    // Campos retornados na Lista
    @SerializedName("id")
    private int id; // O ID da tabela reprocessing

    @SerializedName("saleId")
    private int saleId; // Caso seu backend retorne saleId, senão usa o id

    @SerializedName("clientName")
    private String clientName;

    @SerializedName("saleValue")
    private Double saleValue;

    @SerializedName("errorReason")
    private String errorReason;

    @SerializedName("createdAt")
    private String createdAt;

    // Campos de resposta de sucesso/erro genérico
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    // --- GETTERS ---

    public int getId() {
        // Se saleId vier zerado, tenta usar o id normal
        return saleId > 0 ? saleId : id;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getClientName() {
        return clientName;
    }

    public Double getSaleValue() {
        return saleValue;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}