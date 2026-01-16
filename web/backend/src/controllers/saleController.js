const saleService = require('../services/saleService');
const emailService = require('../services/emailService');

exports.createSale = async (req, res) => {
    try {
        // Validações de Entrada (Input Validation)
        console.log("📥 Body recebido:", req.body);
        const { userId, saleValue, receivedValue } = req.body;
        

        if (!userId) {
            return res.status(400).json({ error: "User ID is required" });
        }
        if (!saleValue || !receivedValue) {
            return res.status(400).json({ error: "Sale Value and Received Value are required" });
        }
        if (parseFloat(saleValue) < 0 || parseFloat(receivedValue) < 0) {
            return res.status(400).json({ error: "Values cannot be negative" });
        }

        // Chama o Service
        const result = await saleService.createNewSale(req.body);
        return res.status(201).json(result);

    } catch (error) {
        console.error("Sale Controller Error:", error);
        // Tratamento de erros personalizados do Service
        const status = error.status || 500;
        const message = error.message || "Internal Server Error";
        return res.status(status).json({ error: message, missing: error.missing });
    }
};

exports.getDashboard = async (req, res) => {
    try {
        const { userId } = req.params;
        const sales = await saleService.getSalesByUser(userId);
        res.json(sales);
    } catch (error) {
        console.error(error);
        const status = error.status || 500;
        res.status(status).json({ error: error.message });
    }
};

exports.getSaleDetails = async (req, res) => {
    try {
        const { id } = req.params;
        const sale = await saleService.getSaleById(id);
        res.json(sale);
    } catch (error) {
        res.status(error.status || 500).json({ error: error.message });
    }
};

// ... (outras funções: createSale, getDashboard, etc) ...

exports.sendReceipt = async (req, res) => {
    try {
        const { saleId } = req.body;

        if (!saleId) {
            return res.status(400).json({ error: "O ID da venda é obrigatório." });
        }
        
        // 1. Busca a venda no banco para pegar os dados (produtos, total, etc)
        // O Sequelize retorna um objeto complexo, usamos .toJSON() se disponível ou o objeto direto
        const saleObj = await saleService.getSaleById(saleId);
        
        // Converte para JSON puro se for objeto do Sequelize para facilitar o template
        const saleData = saleObj.toJSON ? saleObj.toJSON() : saleObj;

        // 2. Verifica se o cliente tem e-mail
        if (!saleData.clientEmail) {
            return res.status(400).json({ error: "Essa venda não tem e-mail de cliente cadastrado." });
        }

        // 3. Chama o serviço de email (aquele com o HTML bonito)
        await emailService.sendSaleReceipt(saleData.clientEmail, saleData);

        return res.status(200).json({ message: "Comprovante enviado com sucesso!" });

    } catch (error) {
        console.error("Erro no Controller de Email:", error);
        return res.status(500).json({ error: "Erro interno ao enviar e-mail." });
    }
};