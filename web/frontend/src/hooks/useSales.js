import { useState, useEffect } from "react";
import { SaleService } from "../services/SaleService";
import { toSaleUI } from "../dtos/saleDTO";

export function useSales() {
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchSales() {
      try {
        setLoading(true);
        const response = await SaleService.getAll();
        
        // Lógica de segurança para extrair o array (mantendo sua lógica original)
        const rawData = Array.isArray(response.data) 
            ? response.data 
            : (response.data.vendas || response.data.data || []);
        
        // Transforma os dados brutos em dados limpos para a UI
        const cleanData = rawData.map(toSaleUI);
        
        setSales(cleanData);
        console.log("Vendas carregadas e formatadas:", cleanData);

      } catch (error) {
        console.error("Erro ao buscar vendas:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchSales();
  }, []);

  return { sales, loading };
}