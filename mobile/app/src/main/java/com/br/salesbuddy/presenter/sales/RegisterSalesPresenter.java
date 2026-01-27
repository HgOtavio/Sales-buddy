package com.br.salesbuddy.presenter.sales;

import android.os.Bundle;

import com.br.salesbuddy.contract.sales.RegisterSalesContract;
import com.br.salesbuddy.utils.parser.InputParser;       // <--- NOVO
import com.br.salesbuddy.utils.parser.SaleBundleFactory; // <--- NOVO

public class RegisterSalesPresenter implements RegisterSalesContract.Presenter {

    private final RegisterSalesContract.View view;

    public RegisterSalesPresenter(RegisterSalesContract.View view) {
        this.view = view;
    }

    @Override
    public void validateAndAdvance(int userId, String nome, String cpf, String email,
                                   String valVendaStr, String valRecebidoStr, String itensConcatenados) {

        // 1. Converte usando o Parser (tira a sujeira do R$)
        double dValVenda = InputParser.parseMoney(valVendaStr);
        double dValRecebido = InputParser.parseMoney(valRecebidoStr);

        // 2. Validação Lógica
        if (dValVenda <= 0) {
            view.showInputError("O Valor da Venda é obrigatório e deve ser maior que zero!");
            return;
        }

        // 3. Cria o pacote usando a Factory
        Bundle bundle = SaleBundleFactory.create(
                userId, nome, cpf, email, dValVenda, dValRecebido, itensConcatenados
        );

        // 4. Navega
        view.navigateToConfirm(bundle);
    }
}