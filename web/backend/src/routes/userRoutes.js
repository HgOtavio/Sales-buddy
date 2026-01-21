const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth');
const userController = require('../controllers/userController');



router.post('/login', userController.login);

router.post('/forgot-password', userController.forgotPassword);

router.post('/reset-password', userController.resetPassword);




router.post('/register', authMiddleware.verifyToken, userController.register);


router.get('/users', authMiddleware.verifyToken, userController.getAllUsers);

router.put('/users', authMiddleware.verifyToken, userController.updateUser);


router.delete('/users', authMiddleware.verifyToken, userController.deleteUser);

router.get('/verify', authMiddleware.verifyToken, userController.verifySession);

module.exports = router;