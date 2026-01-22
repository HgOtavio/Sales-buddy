import { useState } from 'react';
import api from '../services/api'; 
import { toast } from 'react-toastify';
import { ENDPOINTS } from '../services/endpoints'; 

export function usePdfGenerator() {
  const [isGenerating, setIsGenerating] = useState(false);

  const generatePdf = async (saleId, fileName = 'comprovante') => {

    try {
      setIsGenerating(true);

      const response = await api.post(ENDPOINTS.SALES.DOWNLOAD_PDF, { saleId }, {
        responseType: 'blob' 
      });

   
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${fileName}-${saleId}.pdf`);
      document.body.appendChild(link);
      link.click();
      
      // Limpeza
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);

      toast.success("Download concluído!");

    } catch (error) {
      console.error("ERRO NO DOWNLOAD:", error);
      
      if (error.response && error.response.data instanceof Blob) {
          error.response.data.text().then(text => {
              try {
                  const jsonError = JSON.parse(text);
                  toast.error(jsonError.error || "Erro ao gerar PDF.");
              } catch {
                  toast.error("Erro no servidor ao gerar PDF.");
              }
          });
      } else {
          toast.error("Erro de conexão ao baixar o PDF.");
      }
    } finally {
      setIsGenerating(false);
    }
  };

  return { generatePdf, isGenerating };
}