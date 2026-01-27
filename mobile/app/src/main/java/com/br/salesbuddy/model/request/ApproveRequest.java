package com.br.salesbuddy.model.request;

import com.google.gson.annotations.SerializedName;

public class ApproveRequest {

    @SerializedName("id")
    private int id;

    public ApproveRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}