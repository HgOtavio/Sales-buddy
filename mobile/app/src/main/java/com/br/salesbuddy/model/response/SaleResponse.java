package com.br.salesbuddy.model.response;

import com.google.gson.annotations.SerializedName;

public class SaleResponse {
    @SerializedName("saleId")
    public Long saleId;

    @SerializedName("id")
    public Long id;

    @SerializedName("insertId")
    public Long insertId;

    public long getGeneratedId() {
        if (saleId != null) return saleId;
        if (id != null) return id;
        if (insertId != null) return insertId;
        return 0;
    }
}