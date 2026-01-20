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
    
    // Fallback if the list of items comes with different names
    const listaDeItens = saleData.saleItems || saleData.items || [];

    // Helper to format currency safely
    const formatCurrency = (value) => {
        return parseFloat(value || 0).toFixed(2).replace('.', ',');
    };

    // Generating the list of items
    const itensHtml = listaDeItens.map(item => 
        `<li style="margin-bottom: 8px; border-bottom: 1px dashed #eee; padding-bottom: 8px;">
            <div style="display: flex; justify-content: space-between;">
                <span style="font-weight: bold; color: #333;">${item.quantity}x ${item.productName}</span>
            </div>
            </li>`
    ).join('');

    const mailOptions = {
        from: `"Sales-Buddy" <${process.env.EMAIL_USER}>`,
        to: email, 
        subject: `Comprovante de Venda #${saleData.id}`,
        html: `
            <div style="font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4; padding: 40px 0;">
                <div style="background-color: #ffffff; max-width: 400px; margin: 0 auto; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden;">
                    
                    <div style="background-color: #074A82; height: 10px; width: 100%;"></div>
                    
                    <div style="padding: 30px;">
                        <h2 style="color: #074A82; text-align: center; margin-top: 0; margin-bottom: 5px;">COMPROVANTE</h2>
                        <p style="text-align: center; color: #999; font-size: 14px; margin-bottom: 20px;">Venda #${saleData.id}</p>
                        
                        <hr style="border: 0; border-top: 1px solid #eee; margin-bottom: 20px;">

                        <div style="margin-bottom: 20px;">
                            <p style="margin: 5px 0; font-size: 14px; color: #555;">
                                <strong>Cliente:</strong> <span style="color: #074A82; font-size: 16px;">${saleData.clientName}</span>
                            </p>
                            <p style="margin: 5px 0; font-size: 14px; color: #555;">
                                <strong>CPF:</strong> ${saleData.clientCpf || 'Não informado'}
                            </p>
                        </div>

                        <div style="background-color: #F7F8DE; padding: 15px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #f0f0c0;">
                            <h3 style="margin-top: 0; font-size: 14px; text-transform: uppercase; color: #707070; letter-spacing: 1px;">Itens</h3>
                            <ul style="list-style-type: none; padding: 0; margin: 0;">
                                ${itensHtml || '<li style="color: #777; font-style: italic;">Venda sem descrição de itens.</li>'}
                            </ul>
                        </div>

                        <div style="border-top: 2px solid #074A82; padding-top: 15px;">
                            <table style="width: 100%; font-size: 16px;">
                                <tr>
                                    <td style="color: #707070; padding-bottom: 5px;">Valor Total:</td>
                                    <td style="text-align: right; color: #707070; font-weight: bold;">R$ ${formatCurrency(saleData.saleValue || saleData.totalValue)}</td>
                                </tr>
                                <tr>
                                    <td style="color: #707070; padding-bottom: 5px;">Valor Recebido:</td>
                                    <td style="text-align: right; color: #074A82; font-weight: bold;">R$ ${formatCurrency(saleData.receivedValue)}</td>
                                </tr>
                                <tr>
                                    <td style="color: #707070; padding-top: 5px; border-top: 1px solid #eee;">Troco:</td>
                                    <td style="text-align: right; color: #074A82; font-weight: bold; padding-top: 5px; border-top: 1px solid #eee;">R$ ${formatCurrency(saleData.change)}</td>
                                </tr>
                            </table>
                        </div>

                        <div style="margin-top: 40px; text-align: center;">
                            <p style="color: #074A82; font-weight: bold; margin-bottom: 5px;">Obrigado pela preferência!</p>
                            <p style="color: #999; font-size: 12px; margin-top: 0;">Enviado via <strong>Sales-Buddy App</strong></p>
                        </div>
                    </div>
                    
                    <div style="background-color: #074A82; height: 5px; width: 100%;"></div>
                </div>
                
                <div style="text-align: center; margin-top: 20px; color: #aaa; font-size: 11px;">
                    <p>&copy; ${new Date().getFullYear()} Sales-Buddy. Todos os direitos reservados.</p>
                </div>
            </div>
        `
    };
    
    console.log(`📧 Enviando comprovante estilizado para: ${email}`);
    return transporter.sendMail(mailOptions);
};

module.exports = {
    sendWelcomeTokenEmail,
    sendResetTokenEmail,
    sendSaleReceipt 
};