// No seu Backend Node.js
app.post('/usuarios', async (req, res) => {
    console.log("-----------------------------------------");
    console.log("📡 [WEB] Recebendo pedido de cadastro via React");
    console.log("📦 Dados recebidos:", req.body); // Mostra o JSON que veio do React
    
    // Aqui entra o código do Sequelize para salvar Usuario.create(...)
    
    console.log("✅ [DB] Usuário salvo no banco!");
    res.json({ status: "sucesso", id: 1 });
});

app.post('/vendas', async (req, res) => {
    console.log("-----------------------------------------");
    console.log("📱 [MOBILE] Recebendo nova venda via Java/Android");
    console.log("📦 Dados da venda:", req.body); // Mostra o JSON que veio do Java
    
    // Aqui entra o código do Sequelize para salvar Venda.create(...)
    
    console.log("✅ [DB] Venda salva no banco!");
    res.json({ status: "venda_registrada" });
});