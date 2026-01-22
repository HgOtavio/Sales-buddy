const PDFDocument = require('pdfkit');

const generateReceiptPDF = (saleData) => {
    return new Promise((resolve, reject) => {
        
        const items = saleData.saleItems || saleData.items || [];
        
        const baseHeight = 600; 
        
        const variableHeight = items.length * 30; 
        
        const finalHeight = baseHeight + variableHeight;

        const doc = new PDFDocument({ margin: 30, size: [450, finalHeight] });
        
        let buffers = [];

        doc.on('data', buffers.push.bind(buffers));
        doc.on('end', () => {
            const pdfData = Buffer.concat(buffers);
            resolve(pdfData);
        });
        doc.on('error', (err) => reject(err));

        const COLORS = {
            BACKGROUND: '#fffbe6',
            TEXT: '#707070',
            LABEL: '#888888',
            LINE: '#cccccc'
        };

        const FONTS = {
            REGULAR: 'Helvetica',
            BOLD: 'Helvetica-Bold'
        };

        doc.rect(0, 0, doc.page.width, doc.page.height).fill(COLORS.BACKGROUND);
        doc.fillColor(COLORS.TEXT);

        let currentY = 40; 
        const leftColX = 30;       
        const rightColX = 260;     
        const usableWidth = 390;   

        doc.fontSize(9).font(FONTS.BOLD).fillColor(COLORS.LABEL).text('NOME', leftColX, currentY);
        doc.fontSize(14).font(FONTS.BOLD).fillColor(COLORS.TEXT)
            .text((saleData.clientName || 'Consumidor Final').toUpperCase(), leftColX, currentY + 15, { width: 220 });

        doc.fontSize(9).font(FONTS.BOLD).fillColor(COLORS.LABEL).text('CPF', rightColX, currentY);
        doc.fontSize(14).font(FONTS.BOLD).fillColor(COLORS.TEXT)
            .text(saleData.clientCpf || 'Não informado', rightColX, currentY + 15);

        currentY += 50; 
        doc.fontSize(9).font(FONTS.BOLD).fillColor(COLORS.LABEL).text('EMAIL', leftColX, currentY);
        doc.fontSize(13).font(FONTS.BOLD).fillColor(COLORS.TEXT)
            .text((saleData.clientEmail || '-').toUpperCase(), leftColX, currentY + 15);

        currentY += 40;
        doc.moveTo(leftColX, currentY).lineTo(leftColX + usableWidth, currentY)
            .strokeColor(COLORS.LABEL).lineWidth(1).stroke();
     
        currentY += 20;
        doc.fontSize(10).font(FONTS.BOLD).fillColor(COLORS.LABEL);
        doc.text('ITEM', leftColX, currentY);
        doc.text('DESCRIÇÃO', leftColX + 40, currentY);

        currentY += 25;

        items.forEach((item, index) => {
            const prodName = item.productName || item.name || "Item sem nome";
            const idxStr = String(index + 1).padStart(2, '0');

            doc.fontSize(14).font(FONTS.BOLD).fillColor(COLORS.TEXT).text(idxStr, leftColX, currentY);
            doc.text(prodName, leftColX + 40, currentY, { width: 350 });

            currentY += 25; 
        });

        if (items.length === 0) {
            doc.fontSize(14).font(FONTS.BOLD).fillColor(COLORS.TEXT).text('01', leftColX, currentY);
            doc.text('Venda Avulsa', leftColX + 40, currentY);
            currentY += 25;
        }

        currentY += 15;
        doc.moveTo(leftColX, currentY).lineTo(leftColX + usableWidth, currentY)
            .strokeColor(COLORS.LABEL).lineWidth(1).stroke();

        currentY += 40; 
        
        const formatMoney = (val) => `R$ ${parseFloat(val || 0).toFixed(2).replace('.', ',')}`;

        const valorVenda = parseFloat(saleData.saleValue || saleData.totalValue || 0);
        const valorRecebido = parseFloat(saleData.receivedValue || saleData.amountPaid || saleData.recebido || 0);
        
        let valorTroco = parseFloat(saleData.change || saleData.troco || 0);
        
        if (valorTroco === 0 && valorRecebido > valorVenda) {
            valorTroco = valorRecebido - valorVenda;
        }

        const drawTotalRow = (label, value, y) => {
            doc.fontSize(12).font(FONTS.BOLD).fillColor(COLORS.TEXT).text(label, leftColX, y);
            doc.fontSize(16).font(FONTS.BOLD).fillColor(COLORS.TEXT)
                .text(formatMoney(value), leftColX, y - 2, { align: 'right', width: usableWidth });
        };

        drawTotalRow('Valor venda', valorVenda, currentY);
        currentY += 30;
        
        drawTotalRow('Valor recebido', valorRecebido, currentY);
        currentY += 30;
        
        drawTotalRow('Troco devido', valorTroco, currentY);

        const footerY = doc.page.height - 60; 
        
        doc.fontSize(10).font(FONTS.REGULAR).fillColor(COLORS.LABEL)
          .text(`Venda Nº     `, leftColX, footerY, { continued: true, align: 'center', width: usableWidth })
           .font(FONTS.BOLD).fillColor(COLORS.TEXT)
           .text(saleData.id || saleData.saleId || '000');

        doc.end();
    });
};

module.exports = { generateReceiptPDF };