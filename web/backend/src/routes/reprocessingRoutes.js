const express = require('express');
const router = express.Router();
const ReprocessingController = require('../controllers/reprocessingController'); 
const authMiddleware = require('../middlewares/auth');

router.post('/', authMiddleware.verifyToken, ReprocessingController.create);

router.post('/list', authMiddleware.verifyToken, ReprocessingController.list);

router.post('/approve', authMiddleware.verifyToken, ReprocessingController.approveAndMoveToSale);

module.exports = router;