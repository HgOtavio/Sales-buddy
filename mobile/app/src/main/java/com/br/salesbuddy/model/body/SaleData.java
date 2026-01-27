package com.br.salesbuddy.model.body;

public class SaleData {
    public int userId;
    public String nome;          // Vai para o TextView do Nome do Cliente
    public String cpf;
    public String email;

    // INTOCADO: A sua variável item continua aqui exatamente igual
    public String item;

    public double valorVenda;    // Vai para o TextView do Valor Total
    public double valorRecebido;

    public String dataVenda;
    public boolean pendente;

    public SaleData() {}

    public SaleData(String nome, double valorVenda, String dataVenda, boolean pendente) {
        this.nome = nome;
        this.valorVenda = valorVenda;
        this.dataVenda = dataVenda;
        this.pendente = pendente;
    }


    public static class ItemVenda {
        public String productName;
        public double quantity;
        public double unitPrice;

        public ItemVenda(String productName, double quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}