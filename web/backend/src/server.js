const express = require('express');
const cors = require('cors');
const { conectarBanco } = require('./config/dbConfig');

// Importação das Rotas
const userRoutes = require('./routes/userRoutes'); 
const companyRoutes = require('./routes/companyRoutes.js');
const saleRoutes = require('./routes/salesRoutes');

// Importação dos Middlewares de Segurança/Erro (O arquivo novo)
const { 
    handleJsonSyntaxError, 
    handleUrlErrors, 
    handle404 
} = require('./middlewares/serverMiddleware');

const app = express();

app.use(cors());
app.use(express.json()); 

// --- APLICAÇÃO DOS MIDDLEWARES GLOBAIS ---

// 1. Proteção contra erro de sintaxe JSON (Deve vir logo após express.json)
app.use(handleJsonSyntaxError);

// 2. Proteção contra URL com espaços
app.use(handleUrlErrors);

// --- DEFINIÇÃO DAS ROTAS ---
app.use('/auth', userRoutes);         
app.use('/companies', companyRoutes); 
app.use('/vendas', saleRoutes);

// --- ROTA 404 (Deve ser sempre a última) ---
app.use(handle404);

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    conectarBanco();
});