require('dotenv').config(); // 1. Carrega o .env (TEM QUE SER A 1ª LINHA)
const express = require('express');
const cors = require('cors');

// 2. Importa do arquivo database.js (Corrigi o caminho para ./database)
const { Usuario, Venda, Item, conectarBanco } = require('./Infrastructure/database/db'); 

const app = express();
app.use(cors());
app.use(express.json());

// --- ROTA DE CADASTRO ---
app.post('/cadastro', async (req, res) => {
    try {
        const { nome, empresa, email, cnpj, senha } = req.body;
        const existe = await Usuario.findOne({ where: { email } });
        if (existe) return res.status(400).json({ erro: "Email já existe" });

        const novoUsuario = await Usuario.create({ nome, empresa, email, cnpj, senha });
        res.json({ mensagem: "Sucesso!", usuarioId: novoUsuario.id });
    } catch (erro) {
        res.status(500).json({ erro: "Erro servidor" });
    }
});

// --- ROTA DE LOGIN ---
app.post('/login', async (req, res) => {
    try {
        const { email, senha } = req.body;
        const usuario = await Usuario.findOne({ where: { email } });
        
        if (!usuario || usuario.senha !== senha) {
            return res.status(401).json({ erro: "Login inválido" });
        }
        res.json({ status: "sucesso", usuarioId: usuario.id, nome: usuario.nome });
    } catch (erro) {
        res.status(500).json({ erro: "Erro login" });
    }
});

// --- ROTA DE NOVA VENDA ---
app.post('/vendas', async (req, res) => {
    try {
        const { 
            usuarioId, nome_cliente, cpf_cliente, email_cliente, 
            valor_venda, valor_recebido, itens 
        } = req.body;
        
        console.log(`💰 Nova venda para: ${nome_cliente} - R$ ${valor_venda}`);

        // 1. Cria a Venda
        const novaVenda = await Venda.create({
            UsuarioId: usuarioId,
            nome_cliente, cpf_cliente, email_cliente,
            valor_venda, valor_recebido
        });

        // 2. Cria os Itens (se houver)
        if (itens && itens.length > 0) {
            const listaItens = itens.map(item => ({
                nome: item.nome,
                valor: item.valor,
                VendaId: novaVenda.id
            }));
            await Item.bulkCreate(listaItens);
        }

        res.json({ status: "sucesso", vendaId: novaVenda.id });

    } catch (erro) {
        console.error(erro);
        res.status(500).json({ erro: "Erro ao salvar venda" });
    }
});

// --- ROTA DE DASHBOARD (WEB) ---
app.get('/dashboard/:usuarioId', async (req, res) => {
    try {
        const { usuarioId } = req.params;
        const vendas = await Venda.findAll({
            where: { UsuarioId: usuarioId },
            include: [Item],
            order: [['createdAt', 'DESC']]
        });
        res.json(vendas);
    } catch (erro) {
        res.status(500).json({ erro: "Erro ao buscar vendas" });
    }
});

// Inicia o servidor usando a porta do .env ou 3001
const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`🚀 Server rodando na porta ${PORT}`);
    conectarBanco();
});