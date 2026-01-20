const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
    host: process.env.EMAIL_HOST,
    port: parseInt(process.env.EMAIL_PORT),
    secure: true, 
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    },
    tls: {
        rejectUnauthorized: false
    }
});

const sendWelcomeTokenEmail = async (email, user, token) => { /* ... seu código ... */ };
const sendResetTokenEmail = async (email, token) => { /* ... seu código ... */ };



const sendSaleReceipt = async (email, saleData) => {
    
   
    const listaDeItens = saleData.saleItems || saleData.items || [];

    const itensHtml = listaDeItens.map(item => 
        `<li style="margin-bottom: 5px;">
            <strong>${item.productName}</strong> <br>
            <span style="font-size: 12px; color: #555;">
                ${item.quantity}x R$ ${parseFloat(item.unitPrice).toFixed(2)}
            </span>
        </li>`
    ).join('');

    const mailOptions = {
        from: `"Sale-Buddy" <${process.env.EMAIL_USER}>`,
        to: email, 
        subject: `Comprovante de Venda #${saleData.id}`,
        html: `
            <div style="font-family: sans-serif; padding: 20px; border: 1px solid #ddd; max-width: 500px; margin: 0 auto;">
                
                <h2 style="color: #074A82; text-align: center;">Comprovante de Venda</h2>
                <p style="text-align: center; color: #777;">Venda #${saleData.id}</p>
                <hr style="border: 0; border-top: 1px solid #eee;">

                <p><strong>Cliente:</strong> ${saleData.clientName}</p>
                <p><strong>CPF:</strong> ${saleData.clientCpf || 'Não informado'}</p>

                <div style="background: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                    <h3 style="margin-top: 0; font-size: 16px;">Itens Adquiridos:</h3>
                    <ul style="padding-left: 20px;">
                        ${itensHtml || '<li>Item avulso (sem detalhes)</li>'}
                    </ul>
                </div>

                <div style="text-align: right; font-size: 16px;">
                    <p>Total: <strong>R$ ${parseFloat(saleData.saleValue || saleData.totalValue || 0).toFixed(2)}</strong></p>
                    <p style="font-size: 14px; color: #555;">Pago: R$ ${parseFloat(saleData.receivedValue || 0).toFixed(2)}</p>
                    <p style="font-size: 14px; color: #555;">Troco: R$ ${parseFloat(saleData.change || 0).toFixed(2)}</p>
                </div>

                <hr style="border: 0; border-top: 1px solid #eee; margin-top: 20px;">
                <p style="text-align: center; font-size: 12px; color: #999;">
                    Obrigado pela preferência!<br>
                    Enviado por <strong>Sales-Buddy</strong>
                </p>
            </div>
        `
    };
    
    console.log(`📧 Enviando comprovante para: ${email}`);
    return transporter.sendMail(mailOptions);
};

module.exports = {
    sendWelcomeTokenEmail,
    sendResetTokenEmail,
    sendSaleReceipt 
};