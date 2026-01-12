
// Script simples para simular o Frontend enviando dados
async function testarCadastro() {
    const dados = {
        nome: "Hugo Teste",
        empresa: "Minha Loja 10",
        email: "hugo@teste.com",
        cnpj: "12345678900",
        senha: "minhasenha123"
    };

    console.log("📤 Enviando dados para o servidor...");

    try {
        const resposta = await fetch('http://localhost:3001/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        const json = await resposta.json();
        console.log("📥 Resposta do Servidor:", json);
    } catch (erro) {
        console.error("❌ Erro:", erro);
    }
}

testarCadastro();