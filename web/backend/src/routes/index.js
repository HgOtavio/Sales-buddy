const express = require('express');
const router = express.Router();

const userRoutes = require('./userRoutes'); 
const companyRoutes = require('./companyRoutes');
const saleRoutes = require('./salesRoutes'); 
const reprocessingRoutes = require('./reprocessingRoutes');

const { verifyToken } = require('../middlewares/auth'); 


router.use('/auth', userRoutes);

router.use('/companies', companyRoutes); 
router.use('/sales', saleRoutes);

router.use('/reprocessing', verifyToken, reprocessingRoutes);

module.exports = router;