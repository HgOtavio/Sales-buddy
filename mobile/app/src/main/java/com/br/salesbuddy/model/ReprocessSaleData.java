package com.br.salesbuddy.model;

import java.io.Serializable;

public class ReprocessSaleData implements Serializable {
    private int id;
    private String clientName;
    private double saleValue;
    private String errorReason;
    private String date;

    public ReprocessSaleData(int id, String clientName, double saleValue, String errorReason, String date) {
        this.id = id;
        this.clientName = clientName;
        this.saleValue = saleValue;
        this.errorReason = errorReason;
        this.date = date;
    }

    public int getId() { return id; }
    public String getClientName() { return clientName; }
    public double getSaleValue() { return saleValue; }
    public String getErrorReason() { return errorReason; }
    public String getDate() { return date; }
}