package com.br.salesbuddy.model;

public class SaleItemRequest {
    public String productName;
    public String name;
    public int quantity;
    public double unitPrice;

    public SaleItemRequest(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.name = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}