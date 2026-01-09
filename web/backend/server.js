const express = require('express');

require('./src/Infrastructure/database/db.js');

const app = express();
app.use(express.json());

app.get('/', (req, res) => {
  res.send('Sales Buddy API rodando 🚀');
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`API rodando na porta ${PORT}`);
});
