const express = require('express');
const router = express.Router();
const saleController = require('../controllers/saleController');
const authMiddleware = require('../middlewares/auth'); 

router.post('/', authMiddleware.verifyToken, saleController.createSale);


router.post('/email', authMiddleware.verifyToken, saleController.sendReceipt);

router.get('/', authMiddleware.verifyToken, saleController.getDashboard);

router.get('/:id', authMiddleware.verifyToken, saleController.getSaleDetails);

module.exports = router;