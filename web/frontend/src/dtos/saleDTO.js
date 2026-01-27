// src/dtos/saleDTO.js
import { formatCPF, formatCurrency } from "../utils/formatters";

export const toSaleUI = (apiData) => {
    // 1. Resolve a lista de itens
    const itemsList = apiData.items || apiData.saleItems || [];
    
    // 2. Calcula o total de itens (Regra de Negócio sai da View)
    const totalItens = itemsList.reduce((acc, item) => {
         const quantidade = item.quantity || item.qtd || 0;
         return acc + quantidade;
    }, 0);

    return {
        id: apiData.id,
        // Padroniza os nomes dos campos
        clientName: apiData.clientName || apiData.nome || "Cliente Balcão",
        clientCpf: formatCPF(apiData.clientCpf || apiData.cpf),
        clientEmail: apiData.clientEmail || apiData.email || "-",
        
        // Valores já formatados e calculados
        totalQuantity: totalItens,
        totalValue: formatCurrency(apiData.saleValue || apiData.totalValue || apiData.valor),
        change: formatCurrency(apiData.change || apiData.troco),
        
        // Mantemos os dados originais caso precise passar para o Modal de Comprovante
        originalData: apiData 
    };
};