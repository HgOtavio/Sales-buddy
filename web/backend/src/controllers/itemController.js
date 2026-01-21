const itemService = require('../services/itemService');

const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

exports.listItems = async (req, res) => {
    try {
        // MUDANÇA: ID da venda agora vem no Body
        const errorMsg = validateRequiredFields(req.body, ['saleId']);
        if (errorMsg) {
            return res.status(400).json({ error: "ID obrigatório", message: errorMsg });
        }

        const { saleId } = req.body;

        const items = await itemService.getItemsBySaleId(saleId);
        res.json(items);

    } catch (error) {
        handleError(res, error);
    }
};

exports.addItem = async (req, res) => {
    try {
        // MUDANÇA: O saleId foi adicionado à validação do body junto com os dados do item
        const errorMsg = validateRequiredFields(req.body, ['saleId', 'name', 'price']);
        if (errorMsg) {
            return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });
        }

        // Tudo vem do body agora
        const { saleId, name, price } = req.body;

        const newItem = await itemService.addItemToSale(saleId, { name, price });
        
        return res.status(201).json(newItem);

    } catch (error) {
        handleError(res, error);
    }
};