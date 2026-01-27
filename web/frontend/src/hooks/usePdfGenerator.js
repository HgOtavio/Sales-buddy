import { useState } from 'react';
import { toast } from 'react-toastify';
import { SaleService } from '../services/SaleService'; // <--- Service
import { forceDownload, parseBlobError } from '../utils/browserUtils'; // <--- Utilitários

export function usePdfGenerator() {
  const [isGenerating, setIsGenerating] = useState(false);

  const generatePdf = async (saleId, fileName = 'comprovante') => {
    try {
      setIsGenerating(true);

      // 1. SERVICE: Busca os dados brutos
      const response = await SaleService.downloadPdf(saleId);

      // 2. UTILS: Força o navegador a baixar
      forceDownload(response.data, `${fileName}-${saleId}.pdf`);

      toast.success("Download concluído!");

    } catch (error) {
      console.error("ERRO NO DOWNLOAD:", error);
      
      // 3. UTILS: Tenta ler o erro dentro do Blob
      const jsonError = await parseBlobError(error.response);
      
      if (jsonError) {
          toast.error(jsonError.error || "Erro ao gerar PDF.");
      } else {
          toast.error("Erro de conexão ou servidor ao baixar PDF.");
      }
      
    } finally {
      setIsGenerating(false);
    }
  };

  return { generatePdf, isGenerating };
}