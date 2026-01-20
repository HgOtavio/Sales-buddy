
const express = require('express');
const router = express.Router();
const ReprocessingController = require('../controllers/ReprocessingController');



router.post('/', ReprocessingController.create);


router.get('/:userId', ReprocessingController.list);


router.post('/:id/approve', ReprocessingController.approveAndMoveToSale);

module.exports = router;