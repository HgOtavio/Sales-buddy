const PDFDocument = require('pdfkit');

const generateReceiptPDF = (saleData) => {
    return new Promise((resolve, reject) => {
        const doc = new PDFDocument({ margin: 50 });
        let buffers = [];

        doc.on('data', buffers.push.bind(buffers));
        doc.on('end', () => {
            const pdfData = Buffer.concat(buffers);
            resolve(pdfData);
        });
        doc.on('error', (err) => reject(err));

      
        doc.fillColor('#074A82').fontSize(20).text('COMPROVANTE DE VENDA', { align: 'center' });
        doc.moveDown();
        doc.fontSize(10).fillColor('black').text(`Venda #${saleData.id}`, { align: 'center' });
        doc.moveDown();

        doc.moveTo(50, doc.y).lineTo(550, doc.y).stroke();
        doc.moveDown();

        doc.fontSize(12).font('Helvetica-Bold').text('Dados do Cliente');
        doc.font('Helvetica').fontSize(10);
        doc.text(`Nome: ${saleData.clientName || 'Consumidor'}`);
        doc.text(`CPF: ${saleData.clientCpf || 'Não informado'}`);
        doc.text(`Email: ${saleData.clientEmail || '-'}`);
        doc.moveDown();

        doc.fontSize(12).font('Helvetica-Bold').text('Itens da Venda');
        doc.moveDown(0.5);

        const items = saleData.saleItems || saleData.items || [];
        const formatMoney = (val) => `R$ ${parseFloat(val || 0).toFixed(2).replace('.', ',')}`;

        items.forEach((item) => {
            const totalItem = (item.quantity || 1) * (item.price || 0);
            
            doc.font('Helvetica').fontSize(10).text(
                `${item.quantity}x ${item.productName} - ${formatMoney(item.price)} (un)`, 
                { continued: true }
            );
            doc.text(formatMoney(totalItem), { align: 'right' });
        });

        doc.moveDown();
        doc.moveTo(50, doc.y).lineTo(550, doc.y).stroke();
        doc.moveDown();

        const total = saleData.saleValue || saleData.totalValue || 0;
        doc.fontSize(14).font('Helvetica-Bold').fillColor('#074A82');
        doc.text(`TOTAL: ${formatMoney(total)}`, { align: 'right' });

        doc.moveDown(2);
        doc.fontSize(8).fillColor('gray').text('Gerado automaticamente por Sales-Buddy', { align: 'center' });

        doc.end();
    });
};

module.exports = { generateReceiptPDF };