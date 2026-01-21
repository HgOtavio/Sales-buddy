const express = require('express');
const router = express.Router();
const ReprocessingController = require('../controllers/reprocessingController'); 


router.post('/', ReprocessingController.create);


router.post('/list', ReprocessingController.list);


router.post('/approve', ReprocessingController.approveAndMoveToSale);

module.exports = router;