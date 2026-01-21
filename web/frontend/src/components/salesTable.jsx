import { useState, useEffect } from "react";
import api from "../services/api";
import receiptIcon from "../assets/icon-sales.svg";

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(value || 0);
};

const formatCPF = (cpf) => {
  if (!cpf) return "Não informado";
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
};

export function SalesTable({ onViewReceipt }) {
  const [vendas, setVendas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchSales() {
      try {
        const response = await api.get('/vendas'); 
        console.log("DADOS VINDOS DO BACKEND:", response.data); 
        setVendas(response.data);
      } catch (error) {
        console.error("Erro ao buscar vendas", error);
      } finally {
        setLoading(false);
      }
    }

    fetchSales();
  }, []);

  if (loading) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#074A82' }}>Carregando vendas...</div>;
  }

  if (vendas.length === 0) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>Nenhuma venda registrada ainda.</div>;
  }

  return (
    <table className="sales-table">
      <thead>
        <tr>
          <th style={{ textAlign: 'center' }}>ID.VENDA</th>
          <th style={{ textAlign: 'center' }}>NOME</th>
          <th style={{ textAlign: 'center' }}> CPF</th> 
          <th style={{ textAlign: 'center' }}>E-MAIL</th>
          <th style={{ textAlign: 'center' }}>QTD.ITENS </th>
          <th style={{ textAlign: 'center' }}>VALOR</th>
          <th style={{ textAlign: 'center' }}>TROCO</th>
          <th style={{ textAlign: 'center' }}>COMPROVANTE</th> 
        </tr>
      </thead>
      <tbody>
        {vendas.map((venda) => {
          const itemsList = venda.items || venda.saleItems || [];
          const totalItens = itemsList.reduce((acc, item) => {
             const quantidade = item.quantity || item.qtd || 0;
             return acc + quantidade;
          }, 0);

          return (
            <tr key={venda.id}>
              <td className="td-data" style={{ fontWeight: 'bold', textAlign: 'center' }} >{venda.id}</td>
              <td className="td-data" style={{ fontWeight: 'bold',  textAlign: 'center'}}>{venda.clientName || venda.nome || "Cliente Balcão"}</td>
              <td className="td-data" style={{ textAlign: 'center' }}>{formatCPF(venda.clientCpf || venda.cpf)}</td>

              
              <td className="td-data" style={{ fontSize: '13px', textAlign: 'center' }}>
                  {venda.clientEmail || venda.email || "-"}
              </td>

              <td className="td-data" style={{ textAlign: 'center' }}>{totalItens}</td>
              <td className="td-data" style={{ textAlign: 'center' }}>
                 {formatCurrency(venda.saleValue || venda.totalValue || venda.valor)}
              </td>
              <td className="td-data" style={{ textAlign: 'center' }}>
                 {formatCurrency(venda.change || venda.troco)}
              </td>
              
              <td style={{ textAlign: 'center' }}>
                <button 
                  style={{ 
                      background: 'transparent', 
                      border: 'none', 
                      outline: 'none',
                      padding: 0,
                      margin: 0,
                      cursor: 'pointer',
                      display: 'inline-flex', 
                      alignItems: 'center',
                      justifyContent: 'center'
                  }}
                  onClick={() => {
                      if (onViewReceipt) onViewReceipt(venda);
                  }}
                  title="Ver Comprovante"
                >
                    <img 
                      src={receiptIcon} 
                      className="icon-receipt" 
                      alt="Ver" 
                      style={{ width: '24px', height: '24px', display: 'block' }}
                    />
                </button>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}