package com.br.salesbuddy.model.request;

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

    public ReprocessingRequest() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public double getSaleValue() { return saleValue; }
    public void setSaleValue(double saleValue) { this.saleValue = saleValue; }

    public double getReceivedValue() { return receivedValue; }
    public void setReceivedValue(double receivedValue) { this.receivedValue = receivedValue; }

    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }

    public String getErrorReason() { return errorReason; }
    public void setErrorReason(String errorReason) { this.errorReason = errorReason; }

    public List<ReprocessItemRequest> getItems() { return items; }
    public void setItems(List<ReprocessItemRequest> items) { this.items = items; }

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

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }

        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

        public double getTotalItemPrice() { return totalItemPrice; }
        public void setTotalItemPrice(double totalItemPrice) { this.totalItemPrice = totalItemPrice; }
    }
}