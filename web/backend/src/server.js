const express = require('express');
const cors = require('cors');
const { conectarBanco } = require('./config/dbConfig');


// Importação das rotas
const userRoutes = require('./routes/userRoutes'); 
const companyRoutes = require('./routes/companyRoutes.js'); // <--- 1. Importei aqui

const app = express();

app.use(cors());
app.use(express.json()); 

// Definição dos Endpoints
app.use('/auth', userRoutes);         // Rotas de Login/Registro/User
app.use('/companies', companyRoutes); 

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    conectarBanco();
});