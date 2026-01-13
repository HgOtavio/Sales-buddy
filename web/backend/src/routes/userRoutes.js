const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth');
const userController = require('../controllers/userController');

router.post('/register',authMiddleware, userController.register);
router.post('/login', userController.login);

router.get('/users', authMiddleware, userController.getAllUsers);
router.put('/users/:id', authMiddleware, userController.updateUser);

router.delete('/users/:id', authMiddleware, userController.deleteUser);



module.exports = router;