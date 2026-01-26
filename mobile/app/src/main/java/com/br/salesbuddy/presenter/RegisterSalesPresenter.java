package com.br.salesbuddy.presenter;

import android.os.Bundle;

import com.br.salesbuddy.contract.RegisterSalesContract;
import com.br.salesbuddy.utils.MaskUtils;

public class RegisterSalesPresenter implements RegisterSalesContract.Presenter {

    private final RegisterSalesContract.View view;

    public RegisterSalesPresenter(RegisterSalesContract.View view) {
        this.view = view;
    }

    @Override
    public void validateAndAdvance(int userId, String nome, String cpf, String email,
                                   String valVenda, String valRecebido, String itensConcatenados) {

        if (valVenda == null || valVenda.isEmpty() || valVenda.equals("R$ 0,00")) {
            view.showInputError("O Valor da Venda é obrigatório!");
            return;
        }

        //  Limpa as máscaras (tira R$ e pontos, e garante o ponto decimal)
        String cleanValVenda = MaskUtils.unmaskMoney(valVenda).replace(",", ".");
        String cleanValRecebido = MaskUtils.unmaskMoney(valRecebido).replace(",", ".");

        // Converte para DOUBLE puro antes de empacotar
        double dValVenda = 0.0;
        double dValRecebido = 0.0;
        try {
            dValVenda = Double.parseDouble(cleanValVenda);
            dValRecebido = Double.parseDouble(cleanValRecebido);
        } catch (NumberFormatException e) {
            view.showInputError("Erro ao processar valores. Verifique a digitação.");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("ID_DO_LOJISTA", userId);
        bundle.putString("NOME", nome);
        bundle.putString("CPF", cpf);
        bundle.putString("EMAIL", email);

        //  O nome da chave agora é "ITENS_CONCATENADOS" (o ConfirmData esperava isso)
        bundle.putString("ITENS_CONCATENADOS", (itensConcatenados.isEmpty()) ? "Item Diverso" : itensConcatenados);

        //  Agora empacotamos como DOUBLE (putDouble em vez de putString)
        bundle.putDouble("VALOR_VENDA", dValVenda);
        bundle.putDouble("VALOR_RECEBIDO", dValRecebido);

        //  Navegar
        view.navigateToConfirm(bundle);
    }
}