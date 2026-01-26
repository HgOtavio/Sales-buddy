package com.br.salesbuddy.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReprocessingRequest {

    @SerializedName("userId")
    private int userId;

    @SerializedName("clientName")
    private String clientName;

    @SerializedName("saleValue")
    private double saleValue;

    @SerializedName("receivedValue")
    private double receivedValue;

    @SerializedName("change")
    private double change;

    @SerializedName("errorReason")
    private String errorReason;

    @SerializedName("items")
    private List<ReprocessItemRequest> items;

    // Construtor vazio
    public ReprocessingRequest() {}

    // Getters e Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setSaleValue(double saleValue) { this.saleValue = saleValue; }
    public void setReceivedValue(double receivedValue) { this.receivedValue = receivedValue; }
    public void setChange(double change) { this.change = change; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }
    public void setItems(List<ReprocessItemRequest> items) { this.items = items; }

    // --- CLASSE INTERNA PARA OS ITENS ---
    public static class ReprocessItemRequest {
        @SerializedName("productName")
        private String productName;

        @SerializedName("quantity")
        private double quantity;

        @SerializedName("unitPrice")
        private double unitPrice;

        @SerializedName("totalItemPrice")
        private double totalItemPrice;

        public ReprocessItemRequest(String productName, double quantity, double unitPrice, double totalItemPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalItemPrice = totalItemPrice;
        }
    }
}