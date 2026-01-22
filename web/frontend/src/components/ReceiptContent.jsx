import React from 'react';
import { usePdfGenerator } from '../hooks/usePdfGenerator.js'; 

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value || 0);
};

export function ReceiptContent({ data, onClose }) {
  const { generatePdf, isGenerating } = usePdfGenerator();
  
  const venda = data || {};
  const itemsList = venda.items || venda.saleItems || [];

  const receiptData = {
      nome: venda.clientName || venda.nome || "Consumidor Final",
      email: venda.clientEmail || venda.email || "email@naoinformado.com",
      cpf: venda.clientCpf || venda.cpf || "000.000.000-00",
      total: venda.saleValue || venda.totalValue || 0,
      recebido: venda.amountPaid || venda.recebido || (venda.saleValue || 0),
      troco: venda.change || venda.troco || 0,
      id: venda.id || "000"
  };

  return (
    <div className="modal-box receipt-mode">
      <div id="receipt-capture-area">
        <div className="rec-header">
            <div className="rec-col-left">
                <span className="rec-label">NOME</span>
                <span className="rec-name">{receiptData.nome}</span>
                <span className="rec-label" style={{ marginTop: '10px' }}>EMAIL</span>
                <span className="rec-email">{receiptData.email}</span>
            </div>
            <div className="rec-col-right">
                <span className="rec-label">CPF</span>
                <span className="rec-cpf">{receiptData.cpf}</span>
            </div>
        </div>

        <hr className="rec-divider" />

        <div className="rec-list-header">
            <span style={{ width: '40px' }}>ITEM</span>
            <span style={{ flex: 1 }}>DESCRIÇÃO</span>
        </div>
        <div className="rec-list-body">
            {itemsList.length > 0 ? (
                itemsList.map((item, index) => (
                    <div key={index} className="rec-item-row">
                        <span className="rec-item-idx">{String(index + 1).padStart(2, '0')}</span>
                        <span className="rec-item-name">{item.productName || item.name}</span>
                    </div>
                ))
            ) : (
                <div className="rec-item-row">
                    <span className="rec-item-idx">01</span>
                    <span className="rec-item-name">Venda Avulsa</span>
                </div>
            )}
        </div>

        <hr className="rec-divider" style={{ marginTop: '20px' }} />

        <div className="rec-totals-section">
              <div className="rec-total-row">
                <span className="rec-total-label">Valor venda</span>
                <span className="rec-total-value">{formatCurrency(receiptData.recebido)}</span>
            </div>
            <div className="rec-total-row">
                <span className="rec-total-label">Valor recebido</span>
                <span className="rec-total-value">{formatCurrency(receiptData.total)}</span>
            </div>
            <div className="rec-total-row">
                <span className="rec-total-label">Troco devido</span>
                <span className="rec-total-value">{formatCurrency(receiptData.troco)}</span>
            </div>
        </div>
        <div className="rec-footer">Venda Nº <strong>{receiptData.id}</strong></div>
      </div>

      <div className="modal-actions">
        <button 
            className="btn-modal btn-blue" 
            // --- CORREÇÃO AQUI ---
            // Passamos o ID da venda, e não o ID do elemento HTML.
            // O hook vai mandar { saleId: receiptData.id } para o backend.
            onClick={() => generatePdf(receiptData.id, 'comprovante')}
            disabled={isGenerating}
        >
            {isGenerating ? 'Baixando...' : 'Baixar PDF'}
        </button>

        {/* O Imprimir continua local pois usa o navegador */}
        <button className="btn-modal btn-green" onClick={() => window.print()}>Imprimir</button>
        <button className="btn-modal btn-red" onClick={onClose}>Fechar</button>
      </div>
    </div>
  );
}