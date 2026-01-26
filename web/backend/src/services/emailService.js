const nodemailer = require('nodemailer');
const { generateReceiptPDF } = require('../utils/pdfGenerator'); 

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

const sendWelcomeTokenEmail = async (email, userName, token) => {
    console.log(`📧 Enviando email de boas-vindas para: ${email}`);

    const mailOptions = {
        from: `"Sales-Buddy" <${process.env.EMAIL_USER}>`,
        to: email,
        subject: 'Bem-vindo ao Sales-Buddy! Configure sua senha',
        html: `
            <div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px 0;">
                <div style="background-color: #ffffff; max-width: 500px; margin: 0 auto; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden;">
                    <div style="background-color: #074A82; height: 10px; width: 100%;"></div>
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #074A82; text-align: center;">Bem-vindo ao Time!</h2>
                        <p>Olá, <strong>${userName}</strong>!</p>
                        <p>Sua conta foi criada. Copie o token abaixo para ativar sua conta:</p>
                        
                        <div style="background-color: #eef9fd; border: 1px dashed #074A82; padding: 20px; text-align: center; margin: 30px 0; word-break: break-all;">
                            <span style="font-size: 14px; font-weight: bold; color: #333; font-family: monospace;">${token}</span>
                        </div>
                        
                        <p style="font-size: 12px; color: #666; text-align: center;">Este token expira em 24 horas.</p>
                    </div>
                </div>
            </div>
        `
    };

    return transporter.sendMail(mailOptions);
};

const sendResetTokenEmail = async (email, token) => {
    console.log(`📧 Enviando token de reset para: ${email}`);

    const mailOptions = {
        from: `"Suporte Sales-Buddy" <${process.env.EMAIL_USER}>`,
        to: email,
        subject: 'Recuperação de Senha - Sales-Buddy',
        html: `
            <div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px 0;">
                <div style="background-color: #ffffff; max-width: 500px; margin: 0 auto; border-radius: 8px; overflow: hidden;">
                    <div style="background-color: #D32F2F; height: 10px; width: 100%;"></div> 
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #333; text-align: center;">Esqueceu sua senha?</h2>
                        <p>Use o token abaixo para criar uma nova senha:</p>
                        
                        <div style="background-color: #fff0f0; border: 1px dashed #D32F2F; padding: 20px; text-align: center; margin: 30px 0; word-break: break-all;">
                             <span style="font-size: 14px; font-weight: bold; color: #333; font-family: monospace;">${token}</span>
                        </div>
                    </div>
                </div>
            </div>
        `
    };

    return transporter.sendMail(mailOptions);
};

const sendSaleReceipt = async (email, saleData) => {
    console.log(`📧 Gerando PDF e enviando para: ${email}`);

    try {
        
        // 1. Gera o PDF em memória (Buffer)
        const pdfBuffer = await generateReceiptPDF(saleData);

        const htmlBody = `
            <div style="font-family: Arial, sans-serif; color: #333;">
                <h2>Olá!</h2>
                <p>O comprovante da venda <strong>#${saleData.id}</strong> está anexo a este e-mail.</p>
                <p>Obrigado pela preferência!</p>
                <br>
                <p style="color: #999; font-size: 12px;">Equipe Sales-Buddy</p>
            </div>
        `;

        const mailOptions = {
            from: `"Sales-Buddy" <${process.env.EMAIL_USER}>`,
            to: email, 
            subject: `Comprovante de Venda #${saleData.id}`,
            html: htmlBody,
            attachments: [
                {
                    filename: `comprovante-${saleData.id}.pdf`,
                    content: pdfBuffer,
                    contentType: 'application/pdf'
                }
            ]
        };

        return transporter.sendMail(mailOptions);
    } catch (error) {
        console.error("Erro ao gerar ou enviar PDF:", error);
        throw error; 
    }
};

module.exports = {
    sendWelcomeTokenEmail,
    sendResetTokenEmail,
    sendSaleReceipt
};