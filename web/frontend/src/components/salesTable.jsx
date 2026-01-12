import receiptIcon from "../assets/icon-sales.svg";

export function SalesTable({ onViewReceipt }) {
  
const vendas = [
  { 
    id: 1, 
    nome: "Lana", 
    cpf: "123.456.789-00", 
    quantidade: 1, 
    valor: "R$ 150,00", 
    troco: "R$ 50,00",
    receiptUrl: "https://placehold.co/300x500/ffffff/000000?text=COMPROVANTE%0A--------------------%0ACliente:+Lana%0ACPF:+123.456.789-00%0AValor:+R$150,00%0A--------------------%0APAGO" 
  },
  { 
    id: 2, 
    nome: "Chico", 
    cpf: "987.654.321-00", 
    quantidade: 2, 
    valor: "R$ 300,00", 
    troco: "R$ 50,00",
    receiptUrl: "https://placehold.co/300x500/ffffff/000000?text=COMPROVANTE%0A--------------------%0ACliente:+Chico%0ACPF:+987.654.321-00%0AValor:+R$300,00%0A--------------------%0APAGO"
  },
];

  return (
    <table className="sales-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>CPF</th>
          <th>Qtd.</th>
          <th>Valor</th>
          <th>Troco</th>
          <th style={{ textAlign: 'center' }}>Comp.</th> 
        </tr>
      </thead>
      <tbody>
        {vendas.map((venda) => (
          <tr key={venda.id}>
            <td className="td-data">{venda.id}</td>
            <td className="td-data">{venda.nome}</td>
            <td className="td-data">{venda.cpf}</td>
            <td className="td-data">{venda.quantidade}</td>
            <td className="td-data">{venda.valor}</td>
            <td className="td-data">{venda.troco}</td>
            
            <td style={{ textAlign: 'center' }}>
              <button 
                style={{ 
                    background: 'transparent', 
                    border: 'none', 
                    outline: 'none',
                    padding: 0,
                    margin: 0,
                    cursor: 'pointer',
                    position: 'relative', 
                    zIndex: 50,
                    display: 'inline-flex', 
                    alignItems: 'center',
                    justifyContent: 'center'
                }}
                onClick={() => {
                    if (onViewReceipt) onViewReceipt(venda.receiptUrl);
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