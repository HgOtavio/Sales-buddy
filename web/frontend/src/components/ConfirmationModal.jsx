import React, { useEffect } from 'react';
import html2canvas from 'html2canvas'; 
import jsPDF from 'jspdf';
import "../styles/ConfirmationModal.css";

// Função auxiliar para formatar dinheiro
const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(value || 0);
};

export function ConfirmationModal({ 
  isOpen, 
  onClose, 
  onConfirm, 
  userName, 
  variant = "delete", 
  data 
}) {
  
  useEffect(() => {
    if (isOpen && variant === "receipt") {
      console.log(">>> DADOS CHEGANDO NO COMPROVANTE:", data);
    }
  }, [isOpen, variant, data]);
  
  if (!isOpen) return null;

  const handleSavePDF = async () => {
    const element = document.getElementById('receipt-capture-area'); 
    if (!element) return;

    try {
      const canvas = await html2canvas(element, { 
        scale: 2,
        backgroundColor: '#FFFBE6' 
      });

      const imgData = canvas.toDataURL('image/png');

      const pdf = new jsPDF('p', 'mm', 'a4');
      const pdfWidth = pdf.internal.pageSize.getWidth(); 
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width; 

      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
      pdf.save(`comprovante-${data?.id || 'venda'}.pdf`);

    } catch (error) {
      console.error("Erro ao gerar PDF", error);
      alert("Erro ao gerar PDF");
    }
  };

  if (variant === "receipt") {
    const venda = data || {};
    
    const nomeCliente = venda.clientName || venda.nome || venda.cliente || "Consumidor";
    const cpfCliente = venda.clientCpf || venda.cpf || venda.documento || "Não informado";
    const emailCliente = venda.clientEmail || venda.email || "-";
    const valorTotal = venda.saleValue || venda.totalValue || venda.valor || 0;
    
    const itemsList = venda.items || venda.saleItems || venda.itens || [];

    const labelStyle = { color: '#707070', fontSize: '12px', fontWeight: 'bold', marginBottom: '4px' };
    const valueStyle = { color: '#707070', fontSize: '16px', fontWeight: 'normal' };

    return (
      <div className="modal-overlay">
        <div className="modal-box" style={{ maxWidth: '450px', width: '100%', background: '#FFFFFF', padding: '20px', borderRadius: '8px' }}>
          
          {/* CAIXA AMARELA (O QUE VAI VIRAR PDF) */}
          <div id="receipt-capture-area" style={{ backgroundColor: '#FFFBE6', padding: '24px 24px 32px 24px', borderRadius: '4px', marginBottom: '20px', border: '1px solid #EFEFEF' }}>
            
            {/* Header: Nome e CPF */}
            <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', marginBottom: '16px' }}>
                <div style={{ flex: 1.4, paddingRight: '8px' }}>
                    <div style={labelStyle}>Nome do Cliente</div>
                    <div style={valueStyle}>{nomeCliente}</div>
                </div>
                <div style={{ flex: 0.8 }}>
                    <div style={labelStyle}>CPF</div>
                    <div style={{ ...valueStyle, fontSize: '14px' }}>{cpfCliente}</div>
                </div>
            </div>

            {/* Email */}
            <div style={{ marginBottom: '16px' }}>
                <div style={labelStyle}>Email</div>
                <div style={valueStyle}>{emailCliente}</div>
            </div>

            {/* Divisória */}
            <div style={{ height: '1px', backgroundColor: '#707070', marginTop: '16px', marginBottom: '16px', opacity: 0.5 }} />

            {/* Cabeçalho da Lista */}
            <div style={{ display: 'flex', flexDirection: 'row', marginBottom: '8px' }}>
                <div style={{ ...labelStyle, width: '50px' }}>Item</div>
                <div style={{ ...labelStyle, flex: 1 }}>Descrição</div>
                <div style={{ ...labelStyle, width: '80px', textAlign: 'right' }}>Valor</div>
            </div>

            {/* Lista de Itens */}
            <div>
                {itemsList.map((item, index) => {
                    const nomeItem = item.productName || item.name || item.nome || "Item";
                    const qtdItem = item.quantity || item.qtd || 1;
                    const precoItem = item.price || item.unitPrice || item.preco || 0;
                    const totalItem = precoItem * qtdItem;

                    return (
                      <div key={index} style={{ display: 'flex', flexDirection: 'row', marginBottom: '8px', fontSize: '14px', color: '#555' }}>
                          <div style={{ width: '50px' }}>{qtdItem}x</div>
                          <div style={{ flex: 1 }}>{nomeItem}</div>
                          <div style={{ width: '80px', textAlign: 'right' }}>{formatCurrency(totalItem)}</div>
                      </div>
                    );
                })}
                
                {itemsList.length === 0 && (
                   <p style={{ textAlign: 'center', fontSize: '12px', color: '#999' }}>Nenhum item detalhado.</p>
                )}
            </div>

            {/* Total */}
            <div style={{ borderTop: '1px dashed #707070', marginTop: '16px', paddingTop: '16px', textAlign: 'right' }}>
                <span style={{ fontWeight: 'bold', color: '#707070' }}>TOTAL: </span>
                <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#000' }}>
                    {formatCurrency(valorTotal)}
                </span>
            </div>

          </div>
          
          {/* BOTÕES DE AÇÃO */}
          <div className="modal-actions" style={{ justifyContent: 'center', gap: '10px' }}>
            <button className="btn-modal btn-blue" onClick={handleSavePDF}>Salvar PDF</button>
            <button className="btn-modal btn-green" onClick={() => window.print()}>Imprimir</button>
            <button className="btn-modal btn-red" onClick={onClose}>Fechar</button>
          </div>

        </div>
      </div>
    );
  }

  // --- MODO: EXCLUSÃO (PADRÃO) ---
  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h3 style={{ color: '#A32C2C', textAlign: 'left' }}>Você está prestes a excluir:</h3>
        <p><strong>{userName}</strong></p>
        <div className="modal-actions">
          <button className="btn-modal btn-green" onClick={onConfirm}>Sim</button>
          <button className="btn-modal btn-red" onClick={onClose}>Não</button>
        </div>
      </div>
    </div>
  );
}