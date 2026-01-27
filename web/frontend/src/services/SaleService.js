// src/services/SaleService.js
import api from './api'; 
import { ENDPOINTS } from './endpoints'; 

export const SaleService = {
  
  // 1. LISTAR VENDAS (Faltava esse!)
  getAll: async () => {
    // Busca todas as vendas do endpoint base
    return await api.get(ENDPOINTS.SALES.BASE);
  },

  // 2. BAIXAR PDF (Já estava correto)
  downloadPdf: (saleId) => {
    return api.post(
        ENDPOINTS.SALES.DOWNLOAD_PDF, 
        { saleId }, 
        { responseType: 'blob' } // Importante para arquivos binários
    );
  }
};