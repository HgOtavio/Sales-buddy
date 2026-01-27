import { useSales } from "../../hooks/useSales"; // Importe o Hook novo
import receiptIcon from "../../assets/icon-sales.svg";

export function SalesTable({ onViewReceipt }) {
  // O componente pede os dados para o Hook
  const { sales, loading } = useSales();

  if (loading) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#074A82' }}>Carregando vendas...</div>;
  }

  if (sales.length === 0) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>Nenhuma venda registrada ainda.</div>;
  }

  return (
    <table className="sales-table">
      <thead>
        <tr>
          <th style={{ textAlign: 'center' }}>ID.VENDA</th>
          <th style={{ textAlign: 'center' }}>NOME</th>
          <th style={{ textAlign: 'center' }}>CPF</th> 
          <th style={{ textAlign: 'center' }}>E-MAIL</th>
          <th style={{ textAlign: 'center' }}>QTD.ITENS</th>
          <th style={{ textAlign: 'center' }}>VALOR</th>
          <th style={{ textAlign: 'center' }}>TROCO</th>
          <th style={{ textAlign: 'center' }}>COMPROVANTE</th> 
        </tr>
      </thead>
      <tbody>
        {sales.map((venda) => (
          <tr key={venda.id}>
            {/* Agora usamos as propriedades limpas do DTO */}
            <td className="td-data" style={{ fontWeight: 'bold', textAlign: 'center' }}>
                {venda.id}
            </td>
            <td className="td-data" style={{ fontWeight: 'bold', textAlign: 'center'}}>
                {venda.clientName}
            </td>
            <td className="td-data" style={{ textAlign: 'center' }}>
                {venda.clientCpf}
            </td>
            
            <td className="td-data" style={{ fontSize: '13px', textAlign: 'center' }}>
                {venda.clientEmail}
            </td>

            <td className="td-data" style={{ textAlign: 'center' }}>
                {venda.totalQuantity}
            </td>
            <td className="td-data" style={{ textAlign: 'center' }}>
                {venda.totalValue}
            </td>
            <td className="td-data" style={{ textAlign: 'center' }}>
                {venda.change}
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
                    // Passamos os dados originais para o comprovante funcionar
                    if (onViewReceipt) onViewReceipt(venda.originalData);
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
        ))}
      </tbody>
    </table>
  );
}