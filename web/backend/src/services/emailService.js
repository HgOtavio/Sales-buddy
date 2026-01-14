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

const sendWelcomeTokenEmail = async (email, user, token) => {
    const mailOptions = {
        from: `"Sale-Buddy" <${process.env.EMAIL_USER}>`,
        to: email,
        subject: 'Bem-vindo ao Sale-Buddy - Crie sua Senha',
        html: `
            <div style="font-family: sans-serif; padding: 20px;">
                <h2 style="color: #007bff;">Bem-vindo, ${user}!</h2>
                <p>Seu cadastro foi realizado.</p>
                <p>Use este token para criar sua senha:</p>
                <div style="background: #f1f1f1; padding: 15px; font-family: monospace;">
                    ${token}
                </div>
            </div>
        `
    };
    return transporter.sendMail(mailOptions);
};
const sendResetTokenEmail = async (email, token) => {
    const mailOptions = {
        from: `"Suporte Sale-Buddy" <${process.env.EMAIL_USER}>`,
        to: email,
        subject: 'Recuperação de Senha - Sale-Buddy',
        html: `
            <div style="font-family: sans-serif; padding: 20px;">
                <h2 style="color: #dc3545;">Recuperação de Senha</h2>
                <p>Você solicitou a recuperação de senha.</p>
                <p>Seu token de segurança é:</p>
                <div style="background: #f1f1f1; padding: 15px; font-family: monospace;">
                    ${token}
                </div>
            </div>
        `
    };
    return transporter.sendMail(mailOptions);
};

module.exports = {
    sendWelcomeTokenEmail,
    sendResetTokenEmail
};