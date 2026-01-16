const express = require('express');
const router = express.Router();
const saleController = require('../controllers/saleController');
const authMiddleware = require('../middlewares/auth'); // Importante para validar o Token

// 1. Rota para CRIAR Venda (POST http://localhost:3001/vendas)
router.post('/', authMiddleware.verifyToken, saleController.createSale);

// 2. Rota para ENVIAR COMPROVANTE por E-mail (POST http://localhost:3001/vendas/email)
// --- ESSA É A NOVA ROTA QUE O ANDROID VAI CHAMAR ---
router.post('/email', authMiddleware.verifyToken, saleController.sendReceipt);

// 3. Rota para LISTAR Vendas do usuário (GET http://localhost:3001/vendas)
router.get('/', authMiddleware.verifyToken, saleController.getDashboard);

// 4. Rota para pegar detalhes de UMA venda específica (GET http://localhost:3001/vendas/1)
router.get('/:id', authMiddleware.verifyToken, saleController.getSaleDetails);

module.exports = router;