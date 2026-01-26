const saleService = require('../services/saleService');
const emailService = require('../services/emailService');
const { handleError } = require('../utils/errorHandler'); 
const { validateRequiredFields } = require('../utils/validators');
const { generateReceiptPDF } = require('../utils/pdfGenerator'); 

exports.createSale = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['saleValue', 'receivedValue']);
        if (errorMsg) {
            throw { status: 400, message: `Dados Incompletos: ${errorMsg}` };
        }

        const { saleValue, receivedValue, items } = req.body;

        if (parseFloat(saleValue) < 0 || parseFloat(receivedValue) < 0) {
            throw { 
                status: 400, 
                message: "O valor da venda e o valor recebido não podem ser negativos." 
            };
        }

        if (!items || !Array.isArray(items) || items.length === 0) {
            throw { status: 400, message: "A venda não pode ser vazia. Adicione itens." };
        }

        const saleDataToSave = {
            ...req.body,
            userId: req.userId 
        };

        const result = await saleService.createNewSale(saleDataToSave);
        
        if (req.body.clientEmail) {
            const fullSaleData = await saleService.getSaleById(result.saleId);
            emailService.sendSaleReceipt(req.body.clientEmail, fullSaleData)
                .catch(err => console.error("Erro silencioso ao enviar e-mail:", err.message));
        }

        return res.status(201).json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.getDashboard = async (req, res) => {
    try {
        const sales = await saleService.getAllSales(); 
        
        return res.json(sales);

    } catch (error) {
        handleError(res, error);
    }
};

exports.getSaleDetails = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) throw { status: 400, message: `ID obrigatório: ${errorMsg}` };

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
        if (errorMsg) throw { status: 400, message: errorMsg };

        const { saleId } = req.body;
        
        const saleObj = await saleService.getSaleById(saleId);
        
        const saleData = (saleObj && typeof saleObj.toJSON === 'function') ? saleObj.toJSON() : saleObj;

        if (!saleData.clientEmail || saleData.clientEmail.trim() === "") {
            throw { 
                status: 400, 
                message: "Esta venda não possui um e-mail de cliente cadastrado para envio." 
            };
        }

        await emailService.sendSaleReceipt(saleData.clientEmail, saleData);

        return res.status(200).json({ message: "Comprovante enviado com sucesso!" });

    } catch (error) {
        handleError(res, error);
    }
};

exports.downloadReceipt = async (req, res) => {
    try {
        const { saleId } = req.body;

        if (!saleId) throw { status: 400, message: "ID da venda é obrigatório para download." };
        
        const saleObj = await saleService.getSaleById(saleId);
        const saleData = (saleObj && typeof saleObj.toJSON === 'function') ? saleObj.toJSON() : saleObj;

        const pdfBuffer = await generateReceiptPDF(saleData);

        res.set({
            'Content-Type': 'application/pdf',
            'Content-Length': pdfBuffer.length,
            'Content-Disposition': `attachment; filename="comprovante_${saleId}.pdf"`
        });

        res.send(pdfBuffer);

    } catch (error) {
        handleError(res, error);
    }
};