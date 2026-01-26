const express = require('express');
const router = express.Router();
const saleController = require('../controllers/saleController');
const authMiddleware = require('../middlewares/auth'); 

router.post('/create', authMiddleware.verifyToken, saleController.createSale);

router.post('/email', authMiddleware.verifyToken, saleController.sendReceipt);

router.get('/', authMiddleware.verifyToken, saleController.getDashboard);

router.post('/details', authMiddleware.verifyToken, saleController.getSaleDetails);
router.post('/download-pdf', authMiddleware.verifyToken, saleController.downloadReceipt);

module.exports = router;