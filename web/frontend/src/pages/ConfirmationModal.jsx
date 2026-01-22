import React from 'react';
import "../styles/ConfirmationModal.css";
import { DeleteContent } from '../components/DeleteContent';
import { ReceiptContent } from '../components/ReceiptContent';

export function ConfirmationModal({ 
  isOpen, 
  onClose, 
  onConfirm, 
  userName, 
  variant = "delete", 
  data 
}) {
  
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      
      {variant === 'receipt' ? (
        
        <ReceiptContent 
            data={data} 
            onClose={onClose} 
        />
        
      ) : (
        
        <DeleteContent 
            userName={userName} 
            onConfirm={onConfirm} 
            onClose={onClose} 
        />
        
      )}
      
    </div>
  );
}