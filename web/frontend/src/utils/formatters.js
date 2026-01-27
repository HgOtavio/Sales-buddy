
// src/utils/formatters.js

export const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(value || 0);
};

export const formatCPF = (cpf) => {
  if (!cpf) return "Não informado";
  // Remove caracteres não numéricos para evitar erros e aplica a máscara
  return cpf.replace(/\D/g, "")
            .replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
};