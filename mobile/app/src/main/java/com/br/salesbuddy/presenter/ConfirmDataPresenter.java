package com.br.salesbuddy.presenter;

import android.content.Context;
import android.os.Bundle;

import com.br.salesbuddy.contract.ConfirmDataContract;
import com.br.salesbuddy.model.SaleData;
import com.br.salesbuddy.network.SalesService;
import com.br.salesbuddy.utils.FormatUtils;

public class ConfirmDataPresenter implements ConfirmDataContract.Presenter {

    private ConfirmDataContract.View view;
    private SalesService service;
    private SaleData currentSale;
    private Context context;

    public ConfirmDataPresenter(ConfirmDataContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.service = new SalesService();
        this.currentSale = new SaleData();
    }

    @Override
    public void loadInitialData(Bundle extras) {
        if (extras == null) {
            view.showError("Dados da venda não encontrados.");
            return;
        }

        // 1. Extrair dados brutos
        currentSale.userId = extras.getInt("ID_DO_LOJISTA", -1);
        currentSale.nome = extras.getString("NOME");
        currentSale.cpf = extras.getString("CPF");
        currentSale.email = extras.getString("EMAIL");
        currentSale.item = extras.getString("ITEM");

        // 2. Converter valores numéricos com segurança
        try {
            String strVenda = extras.getString("VALOR_VENDA", "0").replace(",", ".");
            String strReceb = extras.getString("VALOR_RECEBIDO", "0").replace(",", ".");

            currentSale.valorVenda = Double.parseDouble(strVenda);
            currentSale.valorRecebido = Double.parseDouble(strReceb);
        } catch (NumberFormatException e) {
            currentSale.valorVenda = 0.0;
            currentSale.valorRecebido = 0.0;
        }

        // 3. Lógica de Troco
        double troco = currentSale.valorRecebido - currentSale.valorVenda;
        if (troco < 0) troco = 0.0;

        // 4. Formatar para exibição
        String nomeDisplay = (currentSale.nome == null || currentSale.nome.isEmpty()) ? "Não informado" : currentSale.nome;
        String cpfDisplay = (currentSale.cpf == null || currentSale.cpf.isEmpty()) ? "-" : currentSale.cpf; // Simplifiquei aqui caso não tenha FormatUtils
        String emailDisplay = (currentSale.email == null || currentSale.email.isEmpty()) ? "-" : currentSale.email;
        String itemDisplay = (currentSale.item == null || currentSale.item.isEmpty()) ? "Item Diverso" : currentSale.item;

        // Tente usar FormatUtils se tiver, senão use String.format
        String vendaFmt = "R$ " + String.format("%.2f", currentSale.valorVenda);
        String recebidoFmt = "R$ " + String.format("%.2f", currentSale.valorRecebido);
        String trocoFmt = "R$ " + String.format("%.2f", troco);

        // 5. Manda pra View
        view.displayData(nomeDisplay, cpfDisplay, emailDisplay, itemDisplay, vendaFmt, recebidoFmt, trocoFmt);
    }

    @Override
    public void confirmSale() {
        view.showLoading();

        // AQUI ESTÁ A MUDANÇA CRUCIAL
        service.sendSale(context, currentSale, new SalesService.SalesCallback() {
            @Override
            public void onSuccess(int idVenda) { // <--- Agora recebe o ID!
                view.hideLoading();
                view.showMessage("Venda Finalizada com Sucesso!");

                Bundle finalBundle = new Bundle();

                // 1. Passa o ID REAL (para o envio de e-mail)
                finalBundle.putInt("ID_VENDA_REAL", idVenda);

                // 2. Passa o ID DO LOJISTA (para o menu funcionar na próxima tela)
                finalBundle.putInt("ID_DO_LOJISTA", currentSale.userId);

                // 3. Dados visuais
                finalBundle.putString("NOME", currentSale.nome);
                finalBundle.putString("CPF", currentSale.cpf);
                finalBundle.putString("EMAIL", currentSale.email);
                finalBundle.putString("ITEM", currentSale.item);
                finalBundle.putDouble("VALOR_VENDA", currentSale.valorVenda);
                finalBundle.putDouble("VALOR_RECEBIDO", currentSale.valorRecebido);

                view.navigateToFinalization(finalBundle);
            }

            @Override
            public void onError(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }
}