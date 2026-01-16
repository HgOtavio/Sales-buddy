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


        String cleanValVenda = MaskUtils.unmaskMoney(valVenda);
        String cleanValRecebido = MaskUtils.unmaskMoney(valRecebido);

        Bundle bundle = new Bundle();
        bundle.putInt("ID_DO_LOJISTA", userId);
        bundle.putString("NOME", nome);
        bundle.putString("CPF", cpf);
        bundle.putString("EMAIL", email);
        bundle.putString("ITEM", (itensConcatenados.isEmpty()) ? "Item Diverso" : itensConcatenados);

        bundle.putString("VALOR_VENDA", cleanValVenda);
        bundle.putString("VALOR_RECEBIDO", cleanValRecebido);

        // 4. Navegar
        view.navigateToConfirm(bundle);
    }
}