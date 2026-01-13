const express = require('express');
const cors = require('cors');
const { conectarBanco } = require('./config/dbConfig');
const userRoutes = require('./routes/userRoutes'); 

const app = express();

app.use(cors());
app.use(express.json()); 

app.use('/auth', userRoutes);

const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    conectarBanco();
});