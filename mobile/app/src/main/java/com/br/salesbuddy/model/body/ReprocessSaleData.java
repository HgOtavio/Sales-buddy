package com.br.salesbuddy.model.body;

public class ReprocessSaleData {

    private int id;
    private String clientName;
    private double saleValue;
    private String errorReason;
    private String date;


    private boolean processed;

    public ReprocessSaleData(int id, String clientName, double saleValue, String errorReason, String date) {
        this.id = id;
        this.clientName = clientName;
        this.saleValue = saleValue;
        this.errorReason = errorReason;
        this.date = date;
        this.processed = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public double getSaleValue() {
        return saleValue;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public String getDate() {
        return date;
    }


    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}