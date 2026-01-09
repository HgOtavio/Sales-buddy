import receiptIcon from "../assets/icon-sales.svg";


export function SalesTable() {
  
  const vendas = [
    { 
      id: 1, 
      nome: "Lana", 
      cpf: "123.456.789-00", 
      quantidade: 1, 
      valor: "R$ 150,00", 
      troco: "R$ 50,00", 
    },
    { 
      id: 2, 
      nome: "Chico", 
      cpf: "987.654.321-00", 
      quantidade: 2, 
      valor: "R$ 300,00", 
      troco: "R$ 50,00", 
    },
  ];

  return (
    <table className="sales-table">
      <thead>
        <tr>
          <th>ID Venda</th>
          <th>Nome</th>
          <th>CPF</th>
          <th>Qtd.</th>
          <th>Valor</th>
          <th>Troco</th>
          <th style={{ textAlign: 'center' }}>Comprovante</th> 
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
              <img 
                src={receiptIcon} 
                className="icon-receipt" 
                alt="Visualizar Comprovante" 
                style={{ cursor: 'pointer' }}
                onClick={() => console.log(`Visualizar comprovante de ${venda.nome}`)}
              />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}