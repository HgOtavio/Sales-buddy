const reprocessingService = require('../services/reprocessingService');
const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

module.exports = {

    // 1. Rota que CRIA o item na lista de erros (chamada quando a venda falha)
    async create(req, res) {
        try {
            // Validação básica
            const errorMsg = validateRequiredFields(req.body, ['clientName', 'saleValue']);
            if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

            const userId = req.user ? req.user.id : req.body.userId;
            
            // Adiciona o userId ao JSON e manda para o Service salvar na tabela temporária
            const data = { ...req.body, userId };

            const result = await reprocessingService.createReprocessingEntry(data);
            return res.status(201).json(result);

        } catch (error) {
            handleError(res, error);
        }
    },

    // 2. Rota para LISTAR os erros pendentes
    async list(req, res) {
        try {
            const userId = req.user ? req.user.id : req.body.userId;
            const list = await reprocessingService.listPending(userId);
            return res.json(list);
        } catch (error) {
            handleError(res, error);
        }
    },

    // 3. Rota que APROVA (Tira de Reprocessamento e cria a Venda Oficial)
    async approveAndMoveToSale(req, res) {
        try {
            const errorMsg = validateRequiredFields(req.body, ['id']);
            if (errorMsg) return res.status(400).json({ error: "ID obrigatório", message: errorMsg });

            const { id } = req.body;
            
            // Chama o service que faz a transação de mover os dados
            const result = await reprocessingService.approveAndMoveToSale(id);

            return res.status(200).json({ 
                message: "Venda recuperada e salva com sucesso!", 
                sale: result 
            });

        } catch (error) {
            handleError(res, error);
        }
    }
};