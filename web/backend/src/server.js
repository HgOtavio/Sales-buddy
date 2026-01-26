const express = require('express');
const cors = require('cors');
const { conectarBanco } = require('./config/dbConfig');

const userRoutes = require('./routes/userRoutes'); 
const companyRoutes = require('./routes/companyRoutes.js');
const saleRoutes = require('./routes/salesRoutes');
const reprocessingRoutes = require('./routes/reprocessingRoutes');

const { 
    handleJsonSyntaxError, 
    handleUrlErrors, 
    handle404 
} = require('./middlewares/serverMiddleware');


const { verifyToken } = require('./middlewares/auth'); 

const app = express();

app.use(cors());
app.use(express.json()); 

app.use(handleJsonSyntaxError);
app.use(handleUrlErrors);

app.use('/auth', userRoutes);        
app.use('/companies', companyRoutes); 
app.use('/sales', saleRoutes);      

app.use('/reprocessing', verifyToken, reprocessingRoutes);

app.use(handle404);

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    conectarBanco();
});