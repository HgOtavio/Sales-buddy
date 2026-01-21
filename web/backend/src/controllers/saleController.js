const saleService = require('../services/saleService');
const emailService = require('../services/emailService');

const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

exports.createSale = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['userId', 'saleValue', 'receivedValue']);
        if (errorMsg) {
            return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });
        }

        const { userId, saleValue, receivedValue } = req.body;

        if (parseFloat(saleValue) < 0 || parseFloat(receivedValue) < 0) {
            return res.status(400).json({ error: "Valores Inválidos", message: "O valor da venda e o valor recebido não podem ser negativos." });
        }

        const result = await saleService.createNewSale(req.body);
        return res.status(201).json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.getDashboard = async (req, res) => {
    try {
        const userId = req.user.id; 
        const sales = await saleService.getSalesByUser(userId);
        res.json(sales);
    } catch (error) {
        handleError(res, error);
    }
};

exports.getSaleDetails = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "ID obrigatório", message: errorMsg });

        const { id } = req.body;
        const sale = await saleService.getSaleById(id);
        res.json(sale);
    } catch (error) {
        handleError(res, error);
    }
};

exports.sendReceipt = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['saleId']);
        if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

        const { saleId } = req.body;
        
        const saleObj = await saleService.getSaleById(saleId);
        
        const saleData = saleObj.toJSON ? saleObj.toJSON() : saleObj;

        if (!saleData.clientEmail) {
            return res.status(400).json({ 
                error: "Impossível Enviar", 
                message: "Esta venda não possui um e-mail de cliente cadastrado." 
            });
        }

        await emailService.sendSaleReceipt(saleData.clientEmail, saleData);

        return res.status(200).json({ message: "Comprovante enviado com sucesso!" });

    } catch (error) {
        handleError(res, error);
    }
};