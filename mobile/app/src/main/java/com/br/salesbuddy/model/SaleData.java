package com.br.salesbuddy.model;

public class SaleData {
    public int userId;
    public String nome;          // Vai para o TextView do Nome do Cliente
    public String cpf;
    public String email;
    public String item;
    public double valorVenda;    // Vai para o TextView do Valor Total
    public double valorRecebido;

    // --- Campos adicionais necessários para o Layout que criamos ---
    public String dataVenda;     // Ex: "19/01/2026 - 14:30"
    public boolean pendente;     // Se true = cor de erro, false = cor normal

    // Construtor vazio
    public SaleData() {}

    // Construtor utilitário para facilitar testes no Presenter
    public SaleData(String nome, double valorVenda, String dataVenda, boolean pendente) {
        this.nome = nome;
        this.valorVenda = valorVenda;
        this.dataVenda = dataVenda;
        this.pendente = pendente;
    }
}