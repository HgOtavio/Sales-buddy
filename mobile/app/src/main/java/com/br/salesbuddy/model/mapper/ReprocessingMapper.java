package com.br.salesbuddy.model.mapper;

import com.br.salesbuddy.model.body.ReprocessSaleData;
import com.br.salesbuddy.model.response.ReprocessingResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReprocessingMapper {

    public static List<ReprocessSaleData> toUiModel(List<ReprocessingResponse> apiList) {
        List<ReprocessSaleData> uiList = new ArrayList<>();
        if (apiList == null) return uiList;

        for (ReprocessingResponse itemApi : apiList) {
            String rawDate = itemApi.getCreatedAt();
            String dataFinal = rawDate != null ? rawDate :
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            String nome = (itemApi.getClientName() != null && !itemApi.getClientName().isEmpty())
                    ? itemApi.getClientName() : "Cliente Não Identificado";

            Double valor = itemApi.getSaleValue() != null ? itemApi.getSaleValue() : 0.0;
            String erro = itemApi.getErrorReason() != null ? itemApi.getErrorReason() : "Erro desconhecido";

            ReprocessSaleData data = new ReprocessSaleData(
                    itemApi.getId(), nome, valor, erro, dataFinal
            );
            data.setProcessed(false); // Vem da API = Pendente
            uiList.add(data);
        }
        return uiList;
    }
}