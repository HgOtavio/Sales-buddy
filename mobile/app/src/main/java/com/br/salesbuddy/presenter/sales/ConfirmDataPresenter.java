package com.br.salesbuddy.presenter.sales;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.sales.ConfirmDataContract;
import com.br.salesbuddy.model.body.SaleData;
import com.br.salesbuddy.model.request.ReprocessingRequest;
import com.br.salesbuddy.model.response.ReprocessingResponse;
import com.br.salesbuddy.network.config.RetrofitClient;
import com.br.salesbuddy.network.service.SalesService;
import com.br.salesbuddy.network.api.ReprocessingApi;
import com.br.salesbuddy.utils.parser.SaleDataParser; // <--- NOVO
import com.br.salesbuddy.model.mapper.SaleMapper;     // <--- NOVO
import com.br.salesbuddy.utils.storage.SalePersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmDataPresenter implements ConfirmDataContract.Presenter {

    private final ConfirmDataContract.View view;
    private final SalesService service;
    private final ReprocessingApi reprocessingApi;
    private SaleData currentSale;
    private final Context context;
    private boolean isRecovery = false;

    public ConfirmDataPresenter(ConfirmDataContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.service = new SalesService();
        this.reprocessingApi = RetrofitClient.getClient().create(ReprocessingApi.class);
        this.currentSale = new SaleData();
    }

    @Override
    public void loadInitialData(Bundle extras) {
        isRecovery = false;

        // 1. Verifica Recuperação
        if (extras == null || !extras.containsKey("VALOR_VENDA")) {
            SaleData savedSale = SalePersistence.getSavedSale(context);
            if (savedSale != null) {
                this.currentSale = savedSale;
                isRecovery = true;
            } else {
                view.showError("Dados da venda não encontrados.");
                return;
            }
        }

        // 2. Se não for recuperação, usa o Parser para ler do Bundle
        if (!isRecovery) {
            this.currentSale = SaleDataParser.fromBundle(extras);
        }

        updateView();
    }

    private void updateView() {
        double troco = currentSale.valorRecebido - currentSale.valorVenda;
        if (troco < 0) troco = 0.0;

        String nomeDisplay = (currentSale.nome == null || currentSale.nome.isEmpty()) ? "Não informado" : currentSale.nome;
        String cpfDisplay = (currentSale.cpf == null || currentSale.cpf.isEmpty()) ? "-" : currentSale.cpf;
        String emailDisplay = (currentSale.email == null || currentSale.email.isEmpty()) ? "-" : currentSale.email;

        String vendaFmt = String.format(Locale.getDefault(), "R$ %.2f", currentSale.valorVenda);
        String recebidoFmt = String.format(Locale.getDefault(), "R$ %.2f", currentSale.valorRecebido);
        String trocoFmt = String.format(Locale.getDefault(), "R$ %.2f", troco);

        List<String> listaFormatada = processarItensParaExibicao(currentSale.item);

        view.displayData(nomeDisplay, cpfDisplay, emailDisplay, listaFormatada, vendaFmt, recebidoFmt, trocoFmt);
    }

    // Mantive aqui pois é lógica de visualização simples
    private List<String> processarItensParaExibicao(String rawItems) {
        List<String> resultado = new ArrayList<>();
        if (rawItems == null || rawItems.isEmpty()) {
            resultado.add("Venda Avulsa / Sem itens");
            return resultado;
        }
        Map<String, Integer> contagem = new HashMap<>();
        for (String s : rawItems.split(",")) {
            String nome = s.trim();
            if (!nome.isEmpty()) contagem.put(nome, contagem.getOrDefault(nome, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            resultado.add(entry.getValue() + "x " + entry.getKey());
        }
        return resultado;
    }

    @Override
    public void confirmSale() {
        view.showLoading();

        if (isRecovery) {
            sendToReprocessing("Recuperação de falha anterior");
        } else {
            service.sendSale(context, currentSale, new SalesService.SalesCallback() {
                @Override
                public void onSuccess(long idVenda) {
                    view.hideLoading();
                    SalePersistence.clear(context);
                    view.showMessage("Venda Finalizada!");
                    finalizeFlow(idVenda);
                }

                @Override
                public void onError(String message) {
                    // Se for erro de validação (400), para. Se for erro de servidor, reprocessa.
                    if (isValidationError(message)) {
                        view.hideLoading();
                        view.showError("Erro nos dados: " + message);
                    } else {
                        sendToReprocessing(message);
                    }
                }
            });
        }
    }

    private boolean isValidationError(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase();
        return m.contains("400") || m.contains("422") ||
                m.contains("inválido") || m.contains("obrigatório") ||
                m.contains("invalid") || m.contains("required");
    }

    private void sendToReprocessing(String originalError) {
        // Usa SessionManager se tiver, ou SharedPreferences direto de forma mais limpa
        String token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("salesToken", "");

        if (token.isEmpty()) {
            handleTotalFailure();
            return;
        }

        // USA O MAPPER AQUI
        ReprocessingRequest request = SaleMapper.toReprocessingRequest(currentSale, originalError);

        reprocessingApi.createReprocessing("Bearer " + token, request).enqueue(new Callback<ReprocessingResponse>() {
            @Override
            public void onResponse(Call<ReprocessingResponse> call, Response<ReprocessingResponse> response) {
                view.hideLoading();
                if (response.isSuccessful()) {
                    SalePersistence.clear(context);
                    view.showMessage("Enviado para Reprocessamento.");
                    view.navigateToReprocessing();
                } else {
                    handleTotalFailure();
                }
            }

            @Override
            public void onFailure(Call<ReprocessingResponse> call, Throwable t) {
                view.hideLoading();
                handleTotalFailure();
            }
        });
    }

    private void handleTotalFailure() {
        SalePersistence.saveSale(context, currentSale);
        view.showMessage("Sem internet. Venda salva no dispositivo.");
        view.navigateToHome();
    }

    private void finalizeFlow(long saleId) {
        Bundle finalBundle = new Bundle();
        finalBundle.putLong("ID_VENDA_REAL", saleId);
        finalBundle.putInt("ID_DO_LOJISTA", currentSale.userId);
        finalBundle.putString("NOME", currentSale.nome);
        finalBundle.putString("CPF", currentSale.cpf);
        finalBundle.putString("EMAIL", currentSale.email);
        finalBundle.putDouble("VALOR_VENDA", currentSale.valorVenda);
        finalBundle.putDouble("VALOR_RECEBIDO", currentSale.valorRecebido);
        double troco = currentSale.valorRecebido - currentSale.valorVenda;
        finalBundle.putDouble("TROCO", troco < 0 ? 0.0 : troco);
        finalBundle.putString("ITENS_CONCATENADOS", currentSale.item);

        view.navigateToFinalization(finalBundle);
    }
}