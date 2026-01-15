const companyController = require('../controllers/companyController');
const authMiddleware = require('../middlewares/auth');


const express = require('express');
const router = express.Router();
router.put('/companies/:id', authMiddleware.verifyToken, companyController.updateCompany);
router.get('/companies/:id', authMiddleware.verifyToken, companyController.getCompanyDetails);

module.exports = router;