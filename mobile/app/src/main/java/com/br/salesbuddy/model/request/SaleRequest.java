package com.br.salesbuddy.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SaleRequest {
    public String userId;
    public String clientName;
    public String clientCpf;
    public String clientEmail;
    public double saleValue;
    public double receivedValue;

    @SerializedName("items")
    public List<SaleItemRequest> items;

    @SerializedName("saleItems")
    public List<SaleItemRequest> saleItems;
}