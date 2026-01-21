const express = require('express');
const router = express.Router();
const companyController = require('../controllers/companyController');
const authMiddleware = require('../middlewares/auth');


router.put('/', authMiddleware.verifyToken, companyController.updateCompany);


router.post('/details', authMiddleware.verifyToken, companyController.getCompanyDetails);

router.get('/', authMiddleware.verifyToken, companyController.getAllCompanies);

module.exports = router;