require('dotenv').config(); // É boa prática carregar variáveis de ambiente no topo
const express = require('express');
const cors = require('cors');
const { conectarBanco } = require('./config/dbConfig');
const routes = require('./routes/index'); 

const { 
    handleJsonSyntaxError, 
    handleUrlErrors, 
    handle404 
} = require('./middlewares/serverMiddleware');

const app = express();

app.use(cors());
app.use(express.json()); 

app.use(handleJsonSyntaxError);
app.use(handleUrlErrors);


app.use(routes); 

app.use(handle404);

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
    console.log(` Server running on port ${PORT}`);
    conectarBanco();
});